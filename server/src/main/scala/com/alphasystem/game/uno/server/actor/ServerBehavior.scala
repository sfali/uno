package com.alphasystem.game.uno.server.actor

import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.cluster.typed.{ClusterSingleton, SingletonActor}
import com.alphasystem.game.uno._
import com.alphasystem.game.uno.server.actor.ServerBehavior.Command

class ServerBehavior private(context: ActorContext[Command])
  extends AbstractBehavior[Command](context) {

  context.log.info("Starting server")
  context.cancelReceiveTimeout()

  import ServerBehavior._

  private var gameToPlayerCount = Map.empty[Int, Int].withDefaultValue(0)
  private var playerToGames = Map.empty[String, Int]
  private var currentGameId = -1

  override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case SignIn(name, replyTo) =>
        playerToGames.get(name) match {
          case Some(_) =>
            // player is already part of different game
            replyTo ! InvalidAccess("AlreadyPartOfDifferentGame")

          case None =>
            // if game doesn't exits we need to create it
            if (currentGameId == -1) {
              currentGameId = 1000 + gameToPlayerCount.keys.size + 1
            }
            val updatedCount = gameToPlayerCount(currentGameId) + 1
            gameToPlayerCount += currentGameId -> updatedCount
            if (updatedCount >= MaxNumberOfPlayers) {
              currentGameId = -1
            }
            playerToGames += name -> currentGameId
            replyTo ! GameAssigned(currentGameId)
        }
        Behaviors.same

      case Shutdown => Behaviors.stopped
    }
}

object ServerBehavior {

  private def apply(): Behavior[Command] =
    Behaviors.setup[Command] { context =>
      new ServerBehavior(context)
    }

  def init(system: ActorSystem[_]): ActorRef[Command] =
    ClusterSingleton(system).init(SingletonActor(ServerBehavior(), "UnoServer"))


  sealed trait Command

  private final case object Shutdown extends Command

  final case class SignIn(name: String, replyTo: ActorRef[Response]) extends Command

  sealed trait Response

  final case class GameAssigned(gameId: Int) extends Response

  final case class InvalidAccess(code: String) extends Response

  final case class InternalError(message: String) extends Response

}
