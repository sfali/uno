package com.alphasystem.game.uno.client.ui

import com.alphasystem.game.uno.client.ui.control.GameView
import com.alphasystem.game.uno.model.Player

class UIController(gameView: GameView) {

  def handleGameJoin(player: Player, otherPlayers: List[Player]): Unit = {
    val allPlayers = otherPlayers.map(_.toPlayerDetail) :+ player.toPlayerDetail.copy(name = "You")
    gameView.playerDetails = allPlayers
  }
}

object UIController {
  def apply(gameView: GameView): UIController = new UIController(gameView)
}
