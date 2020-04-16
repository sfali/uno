package com.alphasystem.game.uno.model.request

import io.circe._

case class RequestEnvelope(requestType: RequestType, payload: RequestPayload)

object RequestEnvelope {
  implicit val RequestEnvelopeDecoder: Decoder[RequestEnvelope] =
    Decoder.forProduct2("type", "payload")(RequestEnvelope.apply)

  implicit val RequestEnvelopeEncoder: Encoder[RequestEnvelope] =
    Encoder.forProduct2("type", "payload")(me => (me.requestType, me.payload))
}