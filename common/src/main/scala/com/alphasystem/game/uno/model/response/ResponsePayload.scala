package com.alphasystem.game.uno.model.response

import com.alphasystem.game.uno.model.Player

sealed trait ResponsePayload

case object Empty extends ResponsePayload

final case class PlayerJoined(player: Player, otherPlayers: List[Player] = Nil) extends ResponsePayload
