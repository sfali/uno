package com.alphasystem.game.uno.model.response

import com.alphasystem.game.uno.model.{Card, Player}

sealed trait ResponsePayload

final case class Empty() extends ResponsePayload

final case class PlayerInfo(player: Player, otherPlayers: List[Player] = Nil) extends ResponsePayload

final case class Message(playerName: Option[String] = None,
                         code: MessageCode,
                         text: Option[String] = None) extends ResponsePayload

final case class ChatMessage(playerName: String, message: String) extends ResponsePayload

final case class Cards(playerName: Option[String] = None, cards: List[Card]) extends ResponsePayload

final case class TossResult(cards: List[Cards]) extends ResponsePayload
