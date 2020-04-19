package com.alphasystem.game.uno.server

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.cluster.typed.Cluster
import com.alphasystem.game.uno.server.actor.GameBehavior
import com.alphasystem.game.uno.server.route.HttpServer
import com.alphasystem.game.uno.server.service.DeckService
import com.typesafe.config.ConfigFactory

object Main {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("local")
    val appName = config.getString("app.name")
    ActorSystem[Nothing](Guardian(DeckService()), appName, config)
  }

  object Guardian {

    sealed trait Command

    private case object Init extends Command

    def apply(deckService: DeckService, liveMode: Boolean = true): Behavior[Nothing] =
      Behaviors.setup[Command] {
        context =>
          context.log.info("Initializing Guardian")

          implicit val system: ActorSystem[_] = context.system
          context.self ! Init

          Behaviors.receiveMessage[Command] {
            _ =>
              Cluster(system)
              val gameActorRef = GameBehavior.init(system, deckService)

              if (liveMode) {
                val httpsSever = HttpServer(gameActorRef)
                httpsSever.start()
              }

              Behaviors.same
          }
      }.narrow
  }

}
