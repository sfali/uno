package com.alphasystem.game.uno.model.response

import io.circe._

case class ResponseEnvelope(`type`: ResponseType, payload: ResponsePayload)

object ResponseEnvelope {
  implicit val ResponseEnvelopeDecoder: Decoder[ResponseEnvelope] =
    Decoder.forProduct2("type", "payload")(ResponseEnvelope.apply)

  implicit val ResponseEnvelopeEncoder: Encoder[ResponseEnvelope] =
    Encoder.forProduct2("type", "payload")(me => (me.`type`, me.payload))
}

