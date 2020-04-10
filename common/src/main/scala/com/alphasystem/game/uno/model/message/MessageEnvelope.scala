package com.alphasystem.game.uno.model.message

import io.circe.{Decoder, Encoder}

case class MessageEnvelope(playerId: Int, messageType: RequestType, payload: RequestPayload)

object MessageEnvelope {
  implicit val MessageEnvelopeDecoder: Decoder[MessageEnvelope] =
    Decoder.forProduct3("player-id", "type", "payload")(MessageEnvelope.apply)

  implicit val MessageEnvelopeEncoder: Encoder[MessageEnvelope] =
    Encoder.forProduct3("player-id", "type", "payload")(me => (me.playerId, me.messageType, me.payload))
}