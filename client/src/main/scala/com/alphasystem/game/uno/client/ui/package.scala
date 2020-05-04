package com.alphasystem.game.uno.client

import com.alphasystem.game.uno.model.{Player, PlayerDetail}

package object ui {

  val NEW_LINE: String = System.lineSeparator()

  implicit class PlayerOps(player: Player) {
    def toPlayerDetail: PlayerDetail = PlayerDetail(player.name)
  }

}
