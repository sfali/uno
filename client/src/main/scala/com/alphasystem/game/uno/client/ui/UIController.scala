package com.alphasystem.game.uno.client.ui

import com.alphasystem.game.uno.client.ui.control.{GameModeSelectionDialog, PlayersView, ToolsView}
import com.alphasystem.game.uno.model.{GameType, Player, PlayerDetail}
import javafx.geometry.Pos
import javafx.util.Duration
import org.controlsfx.control.Notifications
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.ObjectProperty

class UIController(stage: PrimaryStage,
                   playersView: PlayersView,
                   toolsView: ToolsView) {

  private var myPlayer: PlayerDetail = _
  private val _gameType: ObjectProperty[GameType] = ObjectProperty(this, "gameType", null)
  private val gameModeSelectionDialog = new GameModeSelectionDialog(stage)

  toolsView.startGameRequestedProperty.addListener {
    (_, _, nv) =>
      if (nv) {
        val maybeGameMode = gameModeSelectionDialog.showAndWait()
        maybeGameMode match {
          case Some(gameMode) =>
            gameType = gameMode.asInstanceOf[GameType]
            toolsView.enableStartGameButton = false
          case None =>
            // canceled
            toolsView.enableStartGameButton = true
        }
      }
  }

  def gameType: GameType = _gameType.value

  def gameType_=(gameType: GameType): Unit = _gameType.value = gameType

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

  def handleStartGame(enable: Boolean): Unit = {
    toolsView.enableStartGameButton = enable
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
  def apply(stage: PrimaryStage,
            gameView: PlayersView,
            toolsView: ToolsView): UIController = new UIController(stage, gameView, toolsView)
}
