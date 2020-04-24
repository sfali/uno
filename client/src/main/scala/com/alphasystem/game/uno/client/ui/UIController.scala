package com.alphasystem.game.uno.client.ui

import com.alphasystem.game.uno.client.ui.control.PlayersView
import com.alphasystem.game.uno.model.{Player, PlayerDetail}
import javafx.geometry.Pos
import javafx.util.Duration
import org.controlsfx.control.Notifications

class UIController(playersView: PlayersView) {

  private var myPlayer: PlayerDetail = _

  def handleGameJoin(player: Player, otherPlayers: List[Player]): Unit = {
    myPlayer = player.toPlayerDetail
    playersView.myPlayer = myPlayer
    val allPlayers = otherPlayers.map(_.toPlayerDetail) :+ myPlayer
    playersView.playerDetails = allPlayers
  }

  def handlePlayerJoined(player: Player): Unit = {
    playersView.addPlayer(player.toPlayerDetail)
    notifyPlayerMovement(player)
  }

  def handlePlayerLeft(player: Player): Unit = {
    playersView.removePlayer(player.toPlayerDetail)
    notifyPlayerMovement(player, joined = false)
  }

  private def notifyPlayerMovement(player: Player, joined: Boolean = true): Unit = {
    if (player.name != myPlayer.name) {
      val title = if (joined) "Player Joined" else "Player Left"
      val text = if (joined) s"${player.name} has joined the game." else s"${player.name} has left the game."
      Notifications
        .create
        .position(Pos.CENTER)
        .hideAfter(Duration.seconds(5))
        .title(title)
        .text(text)
        .showInformation()
    }
  }
}

object UIController {
  def apply(gameView: PlayersView): UIController = new UIController(gameView)
}
