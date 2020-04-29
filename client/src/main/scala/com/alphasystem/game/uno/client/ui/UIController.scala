package com.alphasystem.game.uno.client.ui

import akka.actor.typed.ActorRef
import com.alphasystem.game.uno.client.ui.control.{GameModeSelectionDialog, PlayersView, ToolsView}
import com.alphasystem.game.uno.model.request.{GameMode, RequestEnvelope, RequestPayload, RequestType}
import com.alphasystem.game.uno.model.{GameType, Player, PlayerDetail}
import javafx.animation.{KeyFrame => JKeyFrame}
import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.util.Duration
import org.controlsfx.control.Notifications
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.JFXApp.PrimaryStage

class UIController(stage: PrimaryStage,
                   inputSource: ActorRef[RequestEnvelope],
                   playersView: PlayersView,
                   toolsView: ToolsView) {

  private var myPlayer: PlayerDetail = _
  private var _gameType: GameType = _
  private lazy val gameModeSelectionDialog = new GameModeSelectionDialog(stage)

  toolsView.startGameRequestedProperty.addListener {
    (_, _, nv) =>
      if (nv) {
        val maybeGameMode = gameModeSelectionDialog.showAndWait()
        maybeGameMode match {
          case Some(gameMode) =>
            toolsView.enableStartGameButton = false
            _gameType = gameMode.asInstanceOf[GameType]
            sendStartGameRequest(_gameType)
          case None =>
            // canceled
            _gameType = null
            toolsView.enableStartGameButton = true
        }
      }
  }

  def gameType: GameType = _gameType

  def gameType_=(gameType: GameType): Unit = _gameType = gameType

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
    if (playersView.numberOfPlayers <= 1) {
      toolsView.enableStartGameButton = false
    }
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

  private def sendStartGameRequest(gameType: GameType): Unit =
    initiateDelayedRequest(RequestType.StartGame, GameMode(gameType))

  private def initiateDelayedRequest(requestType: RequestType,
                                     payload: RequestPayload,
                                     duration: Double = 5): Unit = {
    val delegate = new JKeyFrame(Duration.seconds(duration),
      (_: ActionEvent) => inputSource ! RequestEnvelope(requestType, payload))
    val timeline = new Timeline(2000)
    timeline.keyFrames = Seq(new KeyFrame(delegate))
    timeline.play()
  }
}

object UIController {
  def apply(stage: PrimaryStage,
            inputSource: ActorRef[RequestEnvelope],
            gameView: PlayersView,
            toolsView: ToolsView): UIController = new UIController(stage, inputSource, gameView, toolsView)
}
