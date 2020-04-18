package com.alphasystem.game.uno.model.response

import cats.syntax.functor._
import com.alphasystem.game.uno.model.{Card, Player}
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

sealed trait ResponsePayload

final case class Empty() extends ResponsePayload

final case class PlayerJoined(player: Player, otherPlayers: List[Player] = Nil) extends ResponsePayload

final case class Message(playerName: Option[String] = None,
                         code: MessageCode,
                         text: Option[String] = None) extends ResponsePayload

final case class ChatMessage(playerName: String, message: String) extends ResponsePayload

final case class Cards(playerName: Option[String] = None, cards: List[Card]) extends ResponsePayload

final case class TossResult(cards: List[Cards]) extends ResponsePayload

object ResponsePayload {

  implicit val ResponsePayloadEncoder: Encoder[ResponsePayload] =
    Encoder.instance {
      case event@Empty() => event.asJson
      case event@TossResult(_) => event.asJson
      case event@PlayerJoined(_, _) => event.asJson
      case event@Message(_, _, _) => event.asJson
      case event@ChatMessage(_, _) => event.asJson
      case event@Cards(_, _) => event.asJson
    }

  implicit val ResponsePayloadDecoder: Decoder[ResponsePayload] =
    List[Decoder[ResponsePayload]](
      Decoder[Empty].widen,
      Decoder[PlayerJoined].widen,
      Decoder[Message].widen,
      Decoder[ChatMessage].widen,
      Decoder[Cards].widen,
      Decoder[TossResult].widen
    ).reduceLeft(_ or _)
}
