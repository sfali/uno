package com.alphasystem.game.uno.server.actor

import akka.actor.typed._
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors, StashBuffer, TimerScheduler}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import com.alphasystem.game.uno.model.GameType
import com.alphasystem.game.uno.server.actor.GameBehavior.Command
import com.alphasystem.game.uno.server.model.game.{ApprovalStatus, GameStatus}
import com.alphasystem.game.uno.server.model.{Event, StateInfo}
import com.alphasystem.game.uno.server.service.{DeckService, GameService}

import scala.concurrent.duration._
import scala.language.postfixOps

class GameBehavior private(context: ActorContext[Command],
                           buffer: StashBuffer[Command],
                           timer: TimerScheduler[Command],
                           gameId: Int,
                           deckService: DeckService)
  extends AbstractBehavior[Command](context) {

  import GameBehavior._

  private val gameService = GameService(gameId, deckService)
  private var playersMap: Map[String, Boolean] = Map.empty[String, Boolean].withDefaultValue(false)

  context.setReceiveTimeout(1 minute, Idle)
  context.log.info("Starting game: {}", gameId)

  context.self ! Init

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case Init => buffer.unstashAll(joinGame)

      case GetState(replyTo) =>
        replyTo ! StateInfo(gameService.state)
        Behaviors.same

      case PlayerLeft(name) =>
        // TODO:
        context.log.warn("Player {} is left the game", name)
        Behaviors.same

      case Fail(name, ex) =>
        // TODO:
        context.log.error("Exception occurred in up stream", ex)
        Behaviors.same

      case Idle =>
        // ignore time out
        Behaviors.same

      case Shutdown => Behaviors.stopped

      case other =>
        buffer.stash(other)
        Behaviors.same
    }

  private def joinGame: Behavior[Command] =
    Behaviors.receiveMessagePartial {
      case JoinGame(name, replyTo) => joinGame(name, replyTo)

      case StartGame(name, mode) =>
        context.self ! StartGame(name, mode)
        startGame

      case GetState(replyTo) =>
        replyTo ! StateInfo(gameService.state)
        Behaviors.same

      case PlayerLeft(name) =>
        context.log.warn("Player {} is left the game in 'joinGame'", name)
        // simply remove player from the list
        gameService.removePlayer(name)
        playersMap -= name
        Behaviors.same

      case Fail(name, ex) =>
        // TODO:
        context.log.error("Exception occurred in up stream", ex)
        Behaviors.same

      case Idle =>
        // ignore time out
        Behaviors.same

      case Shutdown => Behaviors.stopped

      case other: GameCommand =>
        context.log.warn("Invalid command: {} in joinGame from: {}", other.getClass.getSimpleName, other.name)
        gameService.illegalAccess(other.name)
        Behaviors.same
    }

  private def startGame: Behavior[Command] =
    Behaviors.receiveMessagePartial[Command] {
      case JoinGame(name, replyTo) =>
        // we will still let the people get into the game
        joinGame(name, replyTo)

      case StartGame(name, mode) =>
        // There is a indication to start game.
        // If we reached max capacity, then start the game
        // Otherwise ask other players for their consent
        if (!gameService.state.hasMinimumCapacity) {
          // start game request came but minimum capacity hasn't reached
          gameService.illegalAccess(name)
          joinGame
        } else if (gameService.state.reachedCapacity) {
          // TODO: start with toss to find who would start the game
          Behaviors.same
        } else {
          gameService.startGame(name, mode)
          timer.startSingleTimer(WaitForConfirmations.getClass.getSimpleName, WaitForConfirmations, 30 seconds)
          startGameApprovals
        }

      case GetState(replyTo) =>
        replyTo ! StateInfo(gameService.state)
        Behaviors.same

      case PlayerLeft(name) =>
        // TODO:
        context.log.warn("Player {} is left the game", name)
        Behaviors.same

      case Fail(name, ex) =>
        // TODO:
        context.log.error("Exception occurred in up stream", ex)
        Behaviors.same

      case Idle =>
        // ignore time out
        Behaviors.same

      case Shutdown => Behaviors.stopped

      case other: GameCommand =>
        context.log.warn("Invalid command: {} in startGame from: {}", other.getClass.getSimpleName, other.name)
        gameService.illegalAccess(other.name)
        Behaviors.same
    }

  private def startGameApprovals: Behavior[Command] =
    Behaviors.receiveMessagePartial {
      case WaitForConfirmations =>
        context.log.warn("Waited for 30 seconds for start game approvals but not all confirmations arrived")
        joinGame

      case ConfirmationApproval(name, _) if !playersMap.contains(name) =>
        context.log.warn("Invalid request for ConfirmationApproval from unknown player: {}", name)
        Behaviors.same

      case ConfirmationApproval(name, approved) =>
        val approvalStatus = gameService.startGameApprovals(name, approved)
        approvalStatus match {
          case ApprovalStatus.Approved =>
            context.log.info("Start game request approved")
            timer.cancel(WaitForConfirmations.getClass.getSimpleName)
            context.self ! NotifyStartGame
            performToss
          case ApprovalStatus.Rejected =>
            context.log.info("Start game request rejected")
            timer.cancel(WaitForConfirmations.getClass.getSimpleName)
            gameService.sendCanStartGameMessage()
            joinGame
          case ApprovalStatus.Waiting => Behaviors.same
        }

      case GetState(replyTo) =>
        replyTo ! StateInfo(gameService.state)
        Behaviors.same

      case PlayerLeft(name) =>
        // TODO:
        context.log.warn("Player {} is left the game", name)
        Behaviors.same

      case Fail(name, ex) =>
        // TODO:
        context.log.error("Exception occurred in up stream", ex)
        Behaviors.same

      case Idle =>
        // ignore time out
        Behaviors.same

      case Shutdown => Behaviors.stopped

      case other: GameCommand =>
        // TODO: handle JoinGame separately
        context.log.warn("Invalid command: {} in startGame from: {}", other.getClass.getSimpleName, other.name)
        gameService.illegalAccess(other.name)
        Behaviors.same
    }

  private def performToss: Behavior[Command] =
    Behaviors.receiveMessagePartial {
      case NotifyStartGame =>
        gameService.notifyGameStart()
        timer.startSingleTimer(PerformToss(), 3 seconds)
        Behaviors.same

      case PerformToss(positions) =>
        gameService.toss(positions) match {
          case head :: Nil =>
            context.log.info("Toss winner: {}", head)
            // single winner, we can start round
            // TODO:
            gameMode
          case ls =>
            // retry for the players with highest cards
            timer.startSingleTimer(PerformToss(ls), 3 seconds)
            Behaviors.same
        }

      case GetState(replyTo) =>
        replyTo ! StateInfo(gameService.state)
        Behaviors.same

      case PlayerLeft(name) =>
        // TODO:
        context.log.warn("Player {} is left the game", name)
        Behaviors.same

      case Fail(name, ex) =>
        // TODO:
        context.log.error("Exception occurred in up stream", ex)
        Behaviors.same

      case Idle =>
        // ignore time out
        Behaviors.same

      case Shutdown => Behaviors.stopped

      case other: GameCommand =>
        // TODO: handle JoinGame separately
        context.log.warn("Invalid command: {} in startGame from: {}", other.getClass.getSimpleName, other.name)
        gameService.illegalAccess(other.name)
        Behaviors.same
    }

  private def gameMode: Behavior[Command] =
    Behaviors.receiveMessagePartial[Command] {
      case GetState(replyTo) =>
        replyTo ! StateInfo(gameService.state)
        Behaviors.same

      case PlayerLeft(name) =>
        // TODO:
        context.log.warn("Player {} is left the game", name)
        Behaviors.same

      case Fail(name, ex) =>
        // TODO:
        context.log.error("Exception occurred in up stream", ex)
        Behaviors.same

      case Idle =>
        if (GameStatus.Started == gameService.state.status) {
          context.log.warn("Did not get response from player '{}' in last one minute", gameService.state.currentPlayer.name)
        }
        Behaviors.same

      case Shutdown => Behaviors.stopped

      case other: GameCommand =>
        // TODO: handle JoinGame separately
        context.log.warn("Invalid command: {} in startGame from: {}", other.getClass.getSimpleName, other.name)
        gameService.illegalAccess(other.name)
        Behaviors.same
    }

  private def joinGame(name: String, replyTo: ActorRef[Event]) = {
    val state = gameService.state
    if (state.reachedCapacity) {
      context.log.info("HERE")
      // TODO: reply with sorry
      Behaviors.same[Command]
    } else {
      gameService.joinGame(name, replyTo)
      playersMap += (name -> true)
      Behaviors.same[Command]
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = {
    case _: PostStop =>
      context.log.info("Shutting down: {}", gameId)
      Behaviors.same
  }
}

object GameBehavior {

  val EntityKey: EntityTypeKey[Command] = EntityTypeKey[Command]("UnoGame")

  private def apply(gameId: Int, deckService: DeckService): Behavior[Command] = {
    Behaviors.setup[Command] {
      context =>
        Behaviors.withStash[Command](10) {
          buffer =>
            Behaviors.withTimers[Command] {
              timer => new GameBehavior(context, buffer, timer, gameId, deckService)
            }
        }
    }
  }

  def init(system: ActorSystem[_], deckService: DeckService): ActorRef[ShardingEnvelope[Command]] =
    ClusterSharding(system)
      .init(Entity(EntityKey)
      (entityContext => GameBehavior(entityContext.entityId.toInt, deckService))
        .withStopMessage(Shutdown))


  sealed trait Command

  sealed trait GameCommand extends Command {
    val name: String
  }

  private final case object Init extends Command

  private final case object Idle extends Command

  final case object Shutdown extends Command

  // Only for testing purpose, not exposed publicly
  final case class GetState(replyTo: ActorRef[Event]) extends Command

  final case class JoinGame(name: String, replyTo: ActorRef[Event]) extends GameCommand

  final case class StartGame(name: String, mode: GameType) extends GameCommand

  final case object WaitForConfirmations extends Command

  final case class ConfirmationApproval(name: String, approved: Boolean) extends GameCommand

  private final case object NotifyStartGame extends Command

  private final case class PerformToss(positions: List[Int] = Nil) extends Command

  final case class PlayerLeft(name: String) extends GameCommand

  final case class Fail(name: String, ex: Throwable) extends GameCommand

}
