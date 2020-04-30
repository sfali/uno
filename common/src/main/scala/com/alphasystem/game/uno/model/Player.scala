package com.alphasystem.game.uno.model

case class Player(name: String)

object Player {
  def apply(name: String): Player = new Player(name)

  def apply(playerDetail: PlayerDetail): Player = Player(playerDetail.name)
}
