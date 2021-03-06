package com.alphasystem.game.uno.client.ui

import akka.Done
import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketUpgradeResponse}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.typed.scaladsl.ActorSource
import com.alphasystem.game.uno.client.ui.control.{CardsView, PlayersView, PlayingAreaView, ToolsView}
import com.alphasystem.game.uno.model.request.{RequestEnvelope, RequestType}
import com.alphasystem.game.uno.model.response.{PlayerInfo, ResponseEnvelope, ResponseType, StartGameRequest, TossResult}
import io.circe.parser._
import io.circe.syntax._
import org.controlsfx.tools.Borders
import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Rectangle2D
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType, TextInputDialog}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Screen

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Client extends JFXApp {

  private implicit val system: ActorSystem = ActorSystem("client")

  import system.dispatcher

  private var inputSource: ActorRef[RequestEnvelope] = _

  private lazy val log = system.log

  private lazy val toolsView = ToolsView()

  private lazy val playersView = PlayersView()

  private lazy val cardsView = CardsView()

  private lazy val playingAreaView = PlayingAreaView()

  private lazy val mainPane = new BorderPane()

  private lazy val controller = UIController(stage, inputSource, mainPane, playersView, toolsView)

  showInputDialog match {
    case Some(playerName) =>
      log.info("Connecting as: {}", playerName)
      run(1000, playerName)
    case None =>
      log.warning("No player name provided, exiting now")
      system.terminate()
      System.exit(-2)
  }

  private def webSocketSource: Source[RequestEnvelope, ActorRef[RequestEnvelope]] =
    ActorSource
      .actorRef[RequestEnvelope](
        completionMatcher = {
          case RequestEnvelope(RequestType.GameEnded, _) =>
            log.info("GameEnded")
            system.terminate()
            System.exit(0)
        },
        failureMatcher = {
          case RequestEnvelope(RequestType.Fail, _) =>
            // should not apply to us
            log.warning("Failed message")
            throw new IllegalArgumentException("?????")
        },
        bufferSize = Int.MaxValue,
        overflowStrategy = OverflowStrategy.fail
      )

  private def webSocketFlow(gameId: Int, playerName: String): Flow[Message, ResponseEnvelope, Future[WebSocketUpgradeResponse]] =
    Http()
      .webSocketClientFlow(s"ws://192.168.0.4:8080/uno/gameId/$gameId/playerName/$playerName")
      .collect {
        case TextMessage.Strict(msg) =>
          decode[ResponseEnvelope](msg) match {
            case Left(error) => throw error
            case Right(responseEnvelope) =>
              log.info("Incoming message: {}", responseEnvelope.`type`)
              responseEnvelope
          }
      }

  private def webSocketSink: Sink[ResponseEnvelope, Future[Done]] =
    Sink.foreach[ResponseEnvelope] {
      responseEnvelope =>
        responseEnvelope.`type` match {
          case ResponseType.GameJoined =>
            val response = responseEnvelope.payload.asInstanceOf[PlayerInfo]
            runLater(controller.handleGameJoin(response.player, response.otherPlayers))
          case ResponseType.NewPlayerJoined =>
            runLater(controller.handlePlayerJoined(responseEnvelope.payload.asInstanceOf[PlayerInfo].player))
          case ResponseType.PlayerLeft =>
            runLater(controller.handlePlayerLeft(responseEnvelope.payload.asInstanceOf[PlayerInfo].player))
          case ResponseType.CanStartGame =>
            runLater(controller.handleStartGame(true))
          case ResponseType.StartGameRequested =>
            val payload = responseEnvelope.payload.asInstanceOf[StartGameRequest]
            runLater(controller.handleStartGameRequested(payload.playerName, payload.mode))
          case ResponseType.InitiatingToss => runLater(controller.handleTossInitiated())
          case ResponseType.TossResult =>
            val payload = responseEnvelope.payload.asInstanceOf[TossResult]
            runLater(controller.handleTossResult(payload.cards, payload.winners))
          case ResponseType.IllegalAccess => ???
          case ResponseType.InformationMessage => ???
          case ResponseType.ConfirmationMessage => ???
          case ResponseType.WarningMessage => ???
          case ResponseType.ErrorMessage => ???
          case ResponseType.ChatMessage => ???
        }
    }

  private def run(gameId: Int, playerName: String): Unit = {
    val wsConnection =
      webSocketSource
        .map {
          command =>
            log.info("Outgoing message: {}", command.requestType)
            TextMessage(command.asJson.deepDropNullValues.noSpaces)
        }
        .viaMat(webSocketFlow(gameId, playerName))(Keep.both)
        .toMat(webSocketSink)(Keep.both)
        .run()
    inputSource = wsConnection._1._1
    val eventualWebSocketUpgradeResponse = wsConnection._1._2
    eventualWebSocketUpgradeResponse
      .onComplete {
        case Success(_) =>
          log.info("connected to server.")
          runLater(showUI())

        case Failure(ex) =>
          log.error("onComplete.Failure", ex)
      }
    eventualWebSocketUpgradeResponse.recover {
      case _ =>
        log.warning("Unable to connect.")
        system.terminate()
        System.exit(-1)
    }
  }

  private def showUI(): Unit = {
    stage = new JFXApp.PrimaryStage {
      title = "UNO"

      private val screen: Screen = Screen.primary
      private val bounds: Rectangle2D = screen.visualBounds

      x = bounds.getMinX
      y = bounds.getMinY
      width = bounds.getWidth
      height = bounds.getHeight
      maximized = true

      scene = new Scene {
        private val topPane = new BorderPane()
        topPane.setTop(toolsView)
        topPane.setBottom(playersView)
        mainPane.setTop(topPane)
        mainPane.setCenter(playingAreaView)
        mainPane.setBottom(Borders.wrap(cardsView).etchedBorder().build().build())
        root = mainPane
        maximized = true

        onCloseRequest = evt => {
          val result =
            new Alert(AlertType.Confirmation) {
              title = "Leaving"
              headerText = "Leaving Game!!"
              contentText = "Are you sure you want to leave this game?"
            }.showAndWait()
          result match {
            case Some(ButtonType.OK) => system.terminate()
            case _ => evt.consume()
          }
        }
      }
    }
  }

  private def showInputDialog: Option[String] = {
    val dialog = new TextInputDialog() {
      title = "Connect"
      headerText = "Enter your name to connect to server."
      contentText = "Please enter your name:"
    }
    dialog.showAndWait()
  }

  private def runLater[R](op: => R): Unit = Platform.runLater(op)
}
