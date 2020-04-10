package com.alphasystem.game.uno.model.message

import io.circe.{Decoder, Encoder}

case class RequestEnvelope(playerId: Int, messageType: RequestType, payload: RequestPayload)

object RequestEnvelope {
  implicit val MessageEnvelopeDecoder: Decoder[RequestEnvelope] =
    Decoder.forProduct3("player-id", "type", "payload")(RequestEnvelope.apply)

  implicit val MessageEnvelopeEncoder: Encoder[RequestEnvelope] =
    Encoder.forProduct3("player-id", "type", "payload")(me => (me.playerId, me.messageType, me.payload))
}