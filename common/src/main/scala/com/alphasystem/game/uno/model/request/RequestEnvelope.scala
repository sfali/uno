package com.alphasystem.game.uno.model.request

import io.circe._
import io.circe.generic.auto._

case class RequestEnvelope(position: Int, requestType: RequestType, payload: RequestPayload)

object RequestEnvelope {
  implicit val RequestEnvelopeDecoder: Decoder[RequestEnvelope] =
    Decoder.forProduct3("position", "type", "payload")(RequestEnvelope.apply)

  implicit val RequestEnvelopeEncoder: Encoder[RequestEnvelope] =
    Encoder.forProduct3("position", "type", "payload")(me => (me.position, me.requestType, me.payload))
}