package com.alphasystem.game.uno.server.actor

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Signal}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import com.alphasystem.game.uno.model._
import com.alphasystem.game.uno.model.game.GameStatus
import com.alphasystem.game.uno.server.actor.GameBehavior.Command
import com.alphasystem.game.uno.server.service.GameService

import scala.concurrent.duration._
import scala.language.postfixOps

class GameBehavior private(context: ActorContext[Command],
                           gameId: Int)
  extends AbstractBehavior[Command](context) {

  import GameBehavior._

  private val gameService = GameService(gameId)

  context.setReceiveTimeout(1 minute, Idle)
  context.log.info("Starting game: {}", gameId)

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case JoinGame(name, replyTo) => joinGame(name, replyTo)

      case StartGame(name) =>
        context.log.info("Got request to start game from: {}", name)
        context.self ! StartGame(name)
        startGame

      case GetState(replyTo) =>
        replyTo ! StateInfo(gameService.state)
        Behaviors.same

      case PlayerLeft(name) =>
        // TODO:
        context.log.warn("Player {} is left the game", name)
        Behaviors.same

      case Fail(ex) =>
        // TODO:
        context.log.error("Exception occurred in up stream", ex)
        Behaviors.same

      case Idle =>
        if (GameStatus.Started == gameService.state.status) {
          context.log.warn("Did not get response from player '{}' in last one minute", gameService.state.currentPlayer.name)
        }
        Behaviors.same

      case Shutdown => Behaviors.stopped

      case other =>
        // TODO: no other type is expecting here
        context.log.warn("Invalid message: {} in OnMessage", other.getClass.getSimpleName)
        Behaviors.same
    }

  private def startGame: Behavior[Command] =
    Behaviors.receiveMessagePartial[Command] {
      case JoinGame(name, replyTo) =>
        // we will still let the people get into the game
        joinGame(name, replyTo, startTriggered = true)

      case StartGame(name) =>
        // There is a indication to start game.
        // If we reached max capacity, then start the game
        // Otherwise ask other players for their consent
        if (gameService.state.reachedCapacity) {
          context.log.warn("Why here")
          // TODO: start with toss to find who would start the game
        } else gameService.startGame(name)
        Behaviors.same

      case GetState(replyTo) =>
        replyTo ! StateInfo(gameService.state)
        Behaviors.same

      case PlayerLeft(name) =>
        // TODO:
        context.log.warn("Player {} is left the game", name)
        Behaviors.same

      case Fail(ex) =>
        // TODO:
        context.log.error("Exception occurred in up stream", ex)
        Behaviors.same

      case Idle =>
        if (GameStatus.Started == gameService.state.status) {
          context.log.warn("Did not get response from player '{}' in last one minute", gameService.state.currentPlayer.name)
        }
        Behaviors.same

      case Shutdown => Behaviors.stopped
    }

  private def gameMode: Behavior[Command] =
    Behaviors.receiveMessagePartial[Command] {
      case JoinGame(_, replyTo) =>
        // we are already in game mode, no more player is allowed tp be added
        // This should not happen
        replyTo ! ErrorEvent(ErrorCode.InvalidStateToJoinGame)
        Behaviors.same

      case PlayerLeft(name) =>
        // TODO:
        context.log.warn("Player {} is left the game", name)
        Behaviors.same

      case Fail(ex) =>
        // TODO:
        context.log.error("Exception occured in up stream", ex)
        Behaviors.same

      case Idle =>
        if (GameStatus.Started == gameService.state.status) {
          context.log.warn("Did not get response from player '{}' in last one minute", gameService.state.currentPlayer.name)
        }
        Behaviors.same

      case Shutdown => Behaviors.stopped
    }

  private def joinGame(name: String,
                       replyTo: ActorRef[Event],
                       startTriggered: Boolean = false) = {
    val state = gameService.state
    if (state.reachedCapacity) {
      context.log.info("HERE")
      // TODO: reply with sorry
      Behaviors.same[Command]
    } else {
      val reachedCapacity = gameService.joinGame(name, replyTo)
      if (reachedCapacity && !startTriggered) {
        context.self ! StartGame(state.players.head.name)
        startGame
      } else Behaviors.same[Command]
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

  private def apply(gameId: Int): Behavior[Command] = {
    Behaviors
      .setup[Command] { context =>
        new GameBehavior(context, gameId)
      }
  }

  def init(system: ActorSystem[_]): ActorRef[ShardingEnvelope[Command]] =
    ClusterSharding(system)
      .init(Entity(EntityKey)
      (entityContext => GameBehavior(entityContext.entityId.toInt))
        .withStopMessage(Shutdown))


  sealed trait Command

  private final case object Idle extends Command

  final case object Shutdown extends Command

  // Only for testing purpose, not exposed publicly
  final case class GetState(replyTo: ActorRef[Event]) extends Command

  final case class JoinGame(name: String, replyTo: ActorRef[Event]) extends Command

  final case class StartGame(name: String) extends Command

  final case class PlayerLeft(name: String) extends Command

  final case class Fail(ex: Throwable) extends Command

}
