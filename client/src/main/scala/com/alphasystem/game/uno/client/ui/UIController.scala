package com.alphasystem.game.uno.client.ui

import com.alphasystem.game.uno.client.ui.control.PlayersView
import com.alphasystem.game.uno.model.Player

class UIController(playersView: PlayersView) {

  def handleGameJoin(player: Player, otherPlayers: List[Player]): Unit = {
    val allPlayers = otherPlayers.map(_.toPlayerDetail) :+ player.toPlayerDetail.copy(name = "You")
    playersView.playerDetails = allPlayers
  }
}

object UIController {
  def apply(gameView: PlayersView): UIController = new UIController(gameView)
}
