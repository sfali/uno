package com.alphasystem.game.uno.server

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.cluster.typed.Cluster
import com.alphasystem.game.uno.server.actor.GameBehavior

object Main {

  def main(args: Array[String]): Unit = {
  }

  object Guardian {

    def apply(): Behavior[Nothing] =
      Behaviors.setup[Nothing] {
        context =>
          context.log.info("Initializing Guardian")

          Cluster(context.system)
          GameBehavior.init(context.system)

          Behaviors.receiveMessage[Nothing] {
            _ => Behaviors.same
          }
      }
  }

}
