package com.alphasystem.game.uno.model.message

import cats.syntax.functor._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

sealed trait MessagePayload

case object Empty extends MessagePayload

object MessagePayload {
  implicit val MessageEnvelopeEncoder: Encoder[MessagePayload] =
    Encoder.instance {
      case command@Empty => command.asJson
    }

  implicit val MessageEnvelopeDecoder: Decoder[MessagePayload] =
    List[Decoder[MessagePayload]](
      Decoder[Empty.type].widen
    ).reduceLeft(_ or _)
}
