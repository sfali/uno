package com.alphasystem.game.uno.model.response

import io.circe._
import io.circe.generic.auto._

case class ResponseEnvelope(playerId: Int, responseType: ResponseType, payload: ResponsePayload)

object ResponseEnvelope {
  implicit val ResponseEnvelopeDecoder: Decoder[ResponseEnvelope] =
    Decoder.forProduct3("player-id", "type", "payload")(ResponseEnvelope.apply)

  implicit val ResponseEnvelopeEncoder: Encoder[ResponseEnvelope] =
    Encoder.forProduct3("player-id", "type", "payload")(me => (me.playerId, me.responseType, me.payload))
}
