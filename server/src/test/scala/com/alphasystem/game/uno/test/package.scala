package com.alphasystem.game.uno

import com.alphasystem.game.uno.model.Player

package object test {

  def createPlayer(position: Int): Player = Player(position, s"Player${position + 1}")
}
