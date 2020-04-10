package com.alphasystem.game.uno.model.response

import io.circe.{Decoder, Encoder}

case class ResponseEnvelope(playerId: Int, responseType: ResponseType, payload: ResponsePayload)

object ResponseEnvelope {
  implicit val MessageEnvelopeDecoder: Decoder[ResponseEnvelope] =
    Decoder.forProduct3("player-id", "type", "payload")(ResponseEnvelope.apply)

  implicit val MessageEnvelopeEncoder: Encoder[ResponseEnvelope] =
    Encoder.forProduct3("player-id", "type", "payload")(me => (me.playerId, me.responseType, me.payload))
}
