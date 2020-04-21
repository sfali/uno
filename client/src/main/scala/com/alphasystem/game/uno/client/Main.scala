package com.alphasystem.game.uno.client

import akka.actor.ActorSystem
import com.alphasystem.game.uno.client.ui.Client

object Main {

  def main(args: Array[String]): Unit = {
    if (args.isEmpty || args.length < 2) {
      throw new IllegalArgumentException("Must provide game id and player name")
    }
    implicit val system: ActorSystem = ActorSystem("client")
    Client(args(0).toInt, args(1))
  }
}
