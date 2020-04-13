package com.alphasystem.game.uno.model.request

sealed trait RequestPayload

case object Empty extends RequestPayload

final case class JoinGame(name: String) extends RequestPayload
