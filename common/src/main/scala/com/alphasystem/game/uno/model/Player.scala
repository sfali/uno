package com.alphasystem.game.uno.model

case class Player(position: Int, name: String)

object Player {
  def apply(position: Int, name: String): Player = new Player(position, name)

  def apply(playerDetail: PlayerDetail): Player = Player(playerDetail.position, playerDetail.name)
}
