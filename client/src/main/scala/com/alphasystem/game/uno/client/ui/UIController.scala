package com.alphasystem.game.uno.client.ui

import akka.actor.typed.ActorRef
import com.alphasystem.game.uno.client.ui.control.{GameModeSelectionDialog, PlayersView, ToolsView, TossResultView}
import com.alphasystem.game.uno.model.request._
import com.alphasystem.game.uno.model.response.Cards
import com.alphasystem.game.uno.model.{GameType, Player, PlayerDetail}
import javafx.animation.{KeyFrame => JKeyFrame}
import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.util.Duration
import org.controlsfx.control.Notifications
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Modality

class UIController(stage: PrimaryStage,
                   inputSource: ActorRef[RequestEnvelope],
                   mainPane: BorderPane,
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

  // incoming requests

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

  def handleStartGameRequested(playerName: String, mode: GameType): Unit = {
    this.gameType = mode
    val acceptButton = new ButtonType("Accept")
    val rejectButton = new ButtonType("Reject")
    val alert = new Alert(AlertType.Confirmation) {
      initOwner(stage)
      initModality(Modality.WindowModal)
      title = "Start Game!"
      headerText = s"Player $playerName has requested to start the game in ${mode.entryName} mode."
      contentText = "Please either accept or reject."
      buttonTypes = Seq(acceptButton, rejectButton)
    }
    val result = alert.showAndWait()
    sendStartGameReply(result.contains(acceptButton))
  }

  def handleTossInitiated(): Unit = {
    Notifications
      .create()
      .position(Pos.TOP_RIGHT)
      .title("Initiating Toss")
      .text(s"In order to determine who would start the first round,$NEW_LINE one card will be " +
        s"drawn for each player.${NEW_LINE}The player with the card with highest value(*) will start the first round," +
        s" for each subsequent round play will move clockwise.${NEW_LINE}In case for tie among two or more players, " +
        s"toss will be done among those players." +
        s"$NEW_LINE* The cards 0 - 9 has values according to their face values," +
        s" action cards word 20 points each while wild cards worth 50 points each.")
      .showInformation()
  }

  def handleTossResult(cards: List[Cards], winners: List[String]): Unit = {
    val tossResultView = TossResultView(myPlayer)
    tossResultView.winners.addAll(winners: _*)
    cards
      .sliding(3, 3)
      .zipWithIndex
      .foreach {
        case (cards, index) =>
          index match {
            case 0 => tossResultView.cardsRow1.addAll(cards: _*)
            case 1 => tossResultView.cardsRow2.addAll(cards: _*)
            case 2 => tossResultView.cardsRow3.addAll(cards: _*)
            case _ => // not applicable
          }
      }
    winners match {
      case head :: Nil =>
        // single winner
        notifySingleWinnerToss(head)
      case _ :: _ :: xs =>
      // multiple winner
      //TODO:
      case Nil => // do nothing
    }
    mainPane.setCenter(tossResultView)
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
        .position(Pos.TOP_RIGHT)
        .hideAfter(Duration.seconds(5))
        .title(title)
        .text(text)
        .showInformation()
    }
  }

  private def notifySingleWinnerToss(playerName: String): Unit = {
    val isMyPlayer = myPlayer.name.equals(playerName)
    val title = "Toss Result"
    val prefix =
      if (isMyPlayer) "Congratulation! you"
      else s"Player $playerName"
    val text = s"$prefix will start the first round. Please wait for your cards to get distributed."
    val notification =
      Notifications
        .create
        .position(Pos.TOP_RIGHT)
        .hideAfter(Duration.seconds(5))
        .title(title)
        .text(text)
    delayedRequest(1, () => notification.showInformation())
  }

  private def notifyMultipleWinners(playerNames: List[String]): Unit = {
    val s =
      playerNames match {
        case Nil =>
          // should not happen
          ""
        case first :: second :: Nil => s"between $first and $second"
        case _ =>

      }
    var text = "Toss has tied"
  }

  // Outgoing requests

  private def sendStartGameRequest(gameType: GameType): Unit =
    initiateDelayedRequest(RequestType.StartGame, GameMode(gameType))

  private def sendStartGameReply(accepted: Boolean): Unit = {
    val requestType = if (accepted) RequestType.StartGameApproved else RequestType.StartGameRejected
    initiateDelayedRequest(requestType, Empty())
  }

  private def initiateDelayedRequest(requestType: RequestType,
                                     payload: RequestPayload,
                                     duration: Double = 5): Unit = {
    /*val delegate = new JKeyFrame(Duration.seconds(duration),
      (_: ActionEvent) => inputSource ! RequestEnvelope(requestType, payload))
    val timeline = new Timeline(2000)
    timeline.keyFrames = Seq(new KeyFrame(delegate))
    timeline.play()*/
    delayedRequest(duration, () => inputSource ! RequestEnvelope(requestType, payload))
  }

  private def delayedRequest(duration: Double, request: () => Unit): Unit = {
    val delegate = new JKeyFrame(Duration.seconds(duration), (_: ActionEvent) => request())
    val timeline = new Timeline(2000)
    timeline.keyFrames = Seq(new KeyFrame(delegate))
    timeline.play()
  }
}

object UIController {
  def apply(stage: PrimaryStage,
            inputSource: ActorRef[RequestEnvelope],
            mainPane: BorderPane,
            playersView: PlayersView,
            toolsView: ToolsView): UIController = new UIController(stage, inputSource, mainPane, playersView, toolsView)
}
