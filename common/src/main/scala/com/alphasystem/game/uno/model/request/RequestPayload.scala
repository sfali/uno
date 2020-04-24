package com.alphasystem.game.uno.model.request

sealed trait RequestPayload

final case class Empty() extends RequestPayload

final case class JoinGame(name: String) extends RequestPayload
