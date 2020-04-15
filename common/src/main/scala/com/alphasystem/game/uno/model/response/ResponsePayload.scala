package com.alphasystem.game.uno.model.response

import cats.syntax.functor._
import com.alphasystem.game.uno.model.Player
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

sealed trait ResponsePayload

final case class Empty() extends ResponsePayload

final case class PlayerJoined(player: Player, otherPlayers: List[Player] = Nil) extends ResponsePayload

final case class Message(playerName: String, code: MessageCode) extends ResponsePayload

final case class ChatMessage(playerName: String, message: String) extends ResponsePayload

object ResponsePayload {

  implicit val ResponsePayloadEncoder: Encoder[ResponsePayload] =
    Encoder.instance {
      case event@Empty() => event.asJson
      case event@PlayerJoined(_, _) => event.asJson
      case event@Message(_, _) => event.asJson
      case event@ChatMessage(_, _) => event.asJson
    }

  implicit val ResponsePayloadDecoder: Decoder[ResponsePayload] =
    List[Decoder[ResponsePayload]](
      Decoder[Empty].widen,
      Decoder[PlayerJoined].widen,
      Decoder[Message].widen,
      Decoder[ChatMessage].widen,
    ).reduceLeft(_ or _)
}
