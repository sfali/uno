package com.alphasystem.game.uno.model.request

import cats.syntax.functor._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

sealed trait RequestPayload

final case class Empty() extends RequestPayload

final case class JoinGame(name: String) extends RequestPayload

object RequestPayload {
  implicit val RequestPayloadEncoder: Encoder[RequestPayload] =
    Encoder.instance {
      case event@Empty() => event.asJson
      case event@JoinGame(_) => event.asJson
    }

  implicit val ResponsePayloadDecoder: Decoder[RequestPayload] =
    List[Decoder[RequestPayload]](
      Decoder[Empty].widen,
      Decoder[JoinGame].widen,
    ).reduceLeft(_ or _)
}
