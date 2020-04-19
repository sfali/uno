package com.alphasystem.game.uno.server.route

import akka.Done
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.{ActorSystem => ClassicActorSystem}
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.http.scaladsl.server.{HttpApp, Route}
import com.alphasystem.game.uno.server.actor.GameBehavior

import scala.util.Try

class HttpServer(gameActorRef: ActorRef[ShardingEnvelope[GameBehavior.Command]])
                (implicit system: ActorSystem[_])
  extends HttpApp {

  override protected def routes: Route = pathPrefix("uno") {
    GameRoute(gameActorRef)
  }

  def start(): Unit = super.startServer("0.0.0.0", 8080, system.toClassic)

  override protected def postServerShutdown(attempt: Try[Done], system: ClassicActorSystem): Unit = {
    super.postServerShutdown(attempt, system)
    system.terminate()
  }
}

object HttpServer {
  def apply(gameActorRef: ActorRef[ShardingEnvelope[GameBehavior.Command]])
           (implicit system: ActorSystem[_]): HttpServer =
    new HttpServer(gameActorRef)(system)
}
