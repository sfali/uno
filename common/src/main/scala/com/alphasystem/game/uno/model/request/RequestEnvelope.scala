package com.alphasystem.game.uno.model.request

import io.circe.{Decoder, Encoder}

case class RequestEnvelope(playerId: Int, messageType: RequestType, payload: RequestPayload)

object RequestEnvelope {
  implicit val RequestEnvelopeDecoder: Decoder[RequestEnvelope] =
    Decoder.forProduct3("player-id", "type", "payload")(RequestEnvelope.apply)

  implicit val RequestEnvelopeEncoder: Encoder[RequestEnvelope] =
    Encoder.forProduct3("player-id", "type", "payload")(me => (me.playerId, me.messageType, me.payload))
}