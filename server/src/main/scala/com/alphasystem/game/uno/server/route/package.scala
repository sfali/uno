package com.alphasystem.game.uno.server

import akka.cluster.sharding.typed.ShardingEnvelope
import com.alphasystem.game.uno.model.request.{GameMode, RequestEnvelope, RequestType}
import com.alphasystem.game.uno.server.actor.GameBehavior

package object route {

  implicit class RequestEnvelopeOps(src: RequestEnvelope) {
    def toCommand(gameId: Int, playerName: String): ShardingEnvelope[GameBehavior.Command] =
      src.requestType match {
        case RequestType.StartGame =>
          val mode = src.payload.asInstanceOf[GameMode]
          ShardingEnvelope(gameId.toString, GameBehavior.StartGame(playerName, mode.`type`))
        case RequestType.StartGameApproved =>
          ShardingEnvelope(gameId.toString, GameBehavior.ConfirmationApproval(playerName, approved = true))
        case RequestType.StartGameRejected =>
          ShardingEnvelope(gameId.toString, GameBehavior.ConfirmationApproval(playerName, approved = false))
        case other => throw new RuntimeException(s"unhandled request type $other")
      }
  }
}
