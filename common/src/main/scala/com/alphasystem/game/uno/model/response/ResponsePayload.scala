package com.alphasystem.game.uno.model.response

import cats.syntax.functor._
import com.alphasystem.game.uno.model.{Card, GameType, Player}
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import java.util.{List => JList}
import scala.jdk.CollectionConverters._

sealed trait ResponsePayload

final case class Empty() extends ResponsePayload

final case class PlayerInfo(player: Player, otherPlayers: List[Player] = Nil) extends ResponsePayload

final case class StartGameRequest(playerName: String, mode: GameType) extends ResponsePayload

/*final case class Message(playerName: Option[String] = None,
                         code: MessageCode,
                         text: Option[String] = None) extends ResponsePayload*/

final case class ChatMessage(playerName: String, message: String) extends ResponsePayload

final case class Cards(playerName: Option[String] = None, cards: List[Card]) extends ResponsePayload

object Cards {
  /*
  * Java API
  */
  def create(playerName: String, cards: JList[Card]): Cards = {
    val maybePlayerName = if (Option(playerName).isEmpty || playerName.trim.isEmpty) None else Some(playerName)
    new Cards(maybePlayerName, cards.asScala.toList)
  }
}

final case class TossResult(cards: List[Cards]) extends ResponsePayload

object ResponsePayload {

  implicit val ResponsePayloadEncoder: Encoder[ResponsePayload] =
    Encoder.instance {
      case event@TossResult(_) => event.asJson
      case event@StartGameRequest(_, _) => event.asJson
      case event@PlayerInfo(_, _) => event.asJson
      case event@ChatMessage(_, _) => event.asJson
      case event@Cards(_, _) => event.asJson
      //case event@Message(_, _, _) => event.asJson
      case event@Empty() => event.asJson
    }

  implicit val ResponsePayloadDecoder: Decoder[ResponsePayload] =
    List[Decoder[ResponsePayload]](
      Decoder[StartGameRequest].widen,
      Decoder[PlayerInfo].widen,
      //Decoder[Message].widen,
      Decoder[ChatMessage].widen,
      Decoder[Cards].widen,
      Decoder[TossResult].widen,
      Decoder[Empty].widen
    ).reduceLeft(_ or _)
}
