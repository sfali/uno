package com.alphasystem.game.uno.server.actor

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import com.alphasystem.game.uno.model._
import com.alphasystem.game.uno.model.game.{GameState, GameStatus}
import com.alphasystem.game.uno.model.response.{PlayerJoined, ResponseEnvelope, ResponseType}
import com.alphasystem.game.uno.server.actor.GameBehavior.Command

import scala.concurrent.duration._
import scala.language.postfixOps

class GameBehavior private(context: ActorContext[Command],
                           gameId: Int)
  extends AbstractBehavior[Command](context) {

  import GameBehavior._

  private var state = GameState(gameId)
  private var playerToActorRefs = Map.empty[Int, ActorRef[Event]]

  context.setReceiveTimeout(1 minute, Idle)
  context.log.info("Starting game: {}", gameId)

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case JoinGame(name, replyTo) =>
        context.log.info("Player '{}' is about to join", name)
        val otherPlayers = state.players.toList
        state = state.addPlayer(name)
        val player = state.player(state.numOfPlayer - 1)
        playerToActorRefs += player.id -> replyTo
        playerToActorRefs
          .foreach {
            case (id, actorRef) =>
              val event =
                if (id == player.id)
                  ResponseEvent(ResponseEnvelope(id, ResponseType.GameJoined, PlayerJoined(player, otherPlayers)))
                else
                  ResponseEvent(ResponseEnvelope(id, ResponseType.NewPlayerJoined, PlayerJoined(player)))
              actorRef ! event
          }
        if (state.reachedCapacity) {
          // TODO: start game
          gameMode
        } else Behaviors.same

      case GetState(replyTo) =>
        replyTo ! StateInfo(state)
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
        if (GameStatus.Started == state.status) {
          context.log.warn("Did not get response from player '{}' in last one minute", state.currentPlayer.name)
        }
        Behaviors.same

      case Shutdown => Behaviors.stopped

      case other =>
        // TODO: no other type is expecting here
        context.log.warn("Invalid message: {}", other.getClass.getSimpleName)
        Behaviors.same
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
        if (GameStatus.Started == state.status) {
          context.log.warn("Did not get response from player '{}' in last one minute", state.currentPlayer.name)
        }
        Behaviors.same

      case Shutdown => Behaviors.stopped
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
