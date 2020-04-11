package com.alphasystem.game

import com.alphasystem.game.uno.model.game.PlayDirection

package object uno {

  val MinNumberOfPlayers: Int = 2

  val MaxNumberOfPlayers: Int = 10

  val DefaultPlayDirection: PlayDirection = PlayDirection.Clockwise

  def nextPlayer(current: Int,
                 numOfPlayer: Int,
                 direction: PlayDirection = DefaultPlayDirection): Int = {
    if (numOfPlayer == 0) 0
    else
      direction match {
        case PlayDirection.Clockwise => (current + 1) % numOfPlayer
        case PlayDirection.CounterClockwise => (current + numOfPlayer - 1) % numOfPlayer
      }
  }

}
