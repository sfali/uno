package com.alphasystem.game.uno.model.response

import io.circe._

case class ResponseEnvelope(position: Int, responseType: ResponseType, payload: ResponsePayload)

object ResponseEnvelope {
  implicit val ResponseEnvelopeDecoder: Decoder[ResponseEnvelope] =
    Decoder.forProduct3("position", "type", "payload")(ResponseEnvelope.apply)

  implicit val ResponseEnvelopeEncoder: Encoder[ResponseEnvelope] =
    Encoder.forProduct3("position", "type", "payload")(me => (me.position, me.responseType, me.payload))
}
