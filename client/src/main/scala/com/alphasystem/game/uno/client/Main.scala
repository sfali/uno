package com.alphasystem.game.uno.client

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.alphasystem.game.uno.client.ui.Client

object Main {

  def main(args: Array[String]): Unit = {
    if (args.isEmpty || args.length < 2) {
      throw new IllegalArgumentException("Must provide game id and player name")
    }
    ActorSystem[Nothing](Guardian(args(0).toInt, args(1)), "client")
  }

  object Guardian {

    def apply(gameId: Int, playerName: String): Behavior[Nothing] =
      Behaviors.setup[Any] {
        context =>
          context.log.info("Guardian started")

          context.self ! "Start"

          Behaviors.receiveMessage[Any] {
            msg =>
              context.log.info(">>>> {}", msg)
              Client(gameId, playerName)(context.system)
              Behaviors.same
          }
      }.narrow
  }

}
