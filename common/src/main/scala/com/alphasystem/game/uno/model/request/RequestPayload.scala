package com.alphasystem.game.uno.model.request

import cats.syntax.functor._
import com.alphasystem.game.uno.model.GameType
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

sealed trait RequestPayload

final case class Empty() extends RequestPayload

final case class JoinGame(name: String) extends RequestPayload

final case class GameMode(`type`: GameType) extends RequestPayload

object RequestPayload {
  implicit val RequestPayloadEncoder: Encoder[RequestPayload] =
    Encoder.instance {
      case event@JoinGame(_) => event.asJson
      case event@GameMode(_) => event.asJson
      case event@Empty() => event.asJson
    }

  implicit val ResponsePayloadDecoder: Decoder[RequestPayload] =
    List[Decoder[RequestPayload]](
      Decoder[JoinGame].widen,
      Decoder[GameMode].widen,
      Decoder[Empty].widen,
    ).reduceLeft(_ or _)
}
