package com.alphasystem.game.uno

import akka.cluster.sharding.typed.ShardingEnvelope
import com.alphasystem.game.uno.model.request.{RequestEnvelope, RequestType}
import com.alphasystem.game.uno.server.actor.GameBehavior

package object routes {

  implicit class RequestEnvelopeOps(src: RequestEnvelope) {
    def toCommand(gameId: Int, playerName: String): ShardingEnvelope[GameBehavior.Command] =
      src.requestType match {
        case RequestType.StartGame => ShardingEnvelope(gameId.toString, GameBehavior.StartGame(playerName))
        case RequestType.StartGameApproved =>
          ShardingEnvelope(gameId.toString, GameBehavior.ConfirmationApproval(playerName, approved = true))
        case RequestType.StartGameRejected =>
          ShardingEnvelope(gameId.toString, GameBehavior.ConfirmationApproval(playerName, approved = false))
        case other => throw new RuntimeException(s"unhandled request type $other")
      }
  }

}
