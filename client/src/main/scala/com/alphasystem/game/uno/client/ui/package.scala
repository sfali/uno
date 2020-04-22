package com.alphasystem.game.uno.client

import com.alphasystem.game.uno.model.{Player, PlayerDetail}

package object ui {

  implicit class PlayerOps(player: Player) {
    def toPlayerDetail: PlayerDetail = PlayerDetail(player.position, player.name)
  }

}
