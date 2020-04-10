package com.alphasystem.game.uno.model.response

import cats.syntax.functor._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

sealed trait ResponsePayload

case object Empty extends ResponsePayload

object ResponsePayload {
  implicit val ResponseEnvelopeEncoder: Encoder[ResponsePayload] =
    Encoder.instance {
      case command@Empty => command.asJson
    }

  implicit val ResponseEnvelopeDecoder: Decoder[ResponsePayload] =
    List[Decoder[ResponsePayload]](
      Decoder[Empty.type].widen
    ).reduceLeft(_ or _)
}
