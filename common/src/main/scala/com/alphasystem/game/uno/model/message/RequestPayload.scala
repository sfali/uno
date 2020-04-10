package com.alphasystem.game.uno.model.message

import cats.syntax.functor._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

sealed trait RequestPayload

case object Empty extends RequestPayload

object RequestPayload {
  implicit val MessageEnvelopeEncoder: Encoder[RequestPayload] =
    Encoder.instance {
      case command@Empty => command.asJson
    }

  implicit val MessageEnvelopeDecoder: Decoder[RequestPayload] =
    List[Decoder[RequestPayload]](
      Decoder[Empty.type].widen
    ).reduceLeft(_ or _)
}
