package com.alphasystem.game.uno.server.route

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.alphasystem.game.uno.server.actor.ServerBehavior
import com.alphasystem.game.uno.server.actor.ServerBehavior.{GameAssigned, InternalError, InvalidAccess}
import io.circe.generic.auto._
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

class ServerRoute private(actorRef: ActorRef[ServerBehavior.Command])
                         (implicit system: ActorSystem[_], timeout: Timeout)
  extends CustomMarshaller {

  private val log = LoggerFactory.getLogger(classOf[ServerRoute])

  private def rout(playerName: String): Route = {
    val eventualResponse = actorRef.ask[ServerBehavior.Response](replyTo => ServerBehavior.SignIn(playerName, replyTo))
    onComplete(eventualResponse) {
      case Success(event: GameAssigned) => complete(event)
      case Success(event: InvalidAccess) => complete(event)
      case Failure(ex) =>
        log.error(s"Internal error occurred. player_name=$playerName", ex)
        complete(InternalError("unable to process your request, please try again later"))
    }
  }
}

object ServerRoute {
  def apply(actorRef: ActorRef[ServerBehavior.Command],
            playerName: String)
           (implicit system: ActorSystem[_], timeout: Timeout): Route =
    new ServerRoute(actorRef)(system, timeout).rout(playerName)
}
