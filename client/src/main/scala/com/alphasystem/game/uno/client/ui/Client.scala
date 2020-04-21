package com.alphasystem.game.uno.client.ui

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketUpgradeResponse}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{CompletionStrategy, OverflowStrategy}
import com.alphasystem.game.uno.model.request.{RequestEnvelope, RequestType}
import com.alphasystem.game.uno.model.response.{ResponseEnvelope, ResponseType}
import io.circe.parser._
import io.circe.syntax._
import scalafx.application
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

  private lazy val log = system.log

  private var inputSource: ActorRef = _

  showInputDialog match {
    case Some(playerName) =>
      log.info("Connecting as: {}", playerName)
      run(1000, playerName)
    case None =>
      log.warning("No player name provided, exiting now")
      system.terminate()
      System.exit(-2)
  }

  private lazy val webSocketSource: Source[Any, ActorRef] =
    Source
      .actorRef(
        completionMatcher = {
          case RequestEnvelope(RequestType.GameEnd, _) => CompletionStrategy.immediately
        },
        failureMatcher = PartialFunction.empty,
        bufferSize = Int.MaxValue,
        overflowStrategy = OverflowStrategy.fail
      )

  private def webSocketFlow(gameId: Int,
                            playerName: String): Flow[Message, ResponseEnvelope, Future[WebSocketUpgradeResponse]] =
    Http()
      .webSocketClientFlow(s"ws://192.168.0.4:8080/uno/gameId/$gameId/playerName/$playerName")
      .collect {
        case TextMessage.Strict(msg) =>
          decode[ResponseEnvelope](msg) match {
            case Left(error) => throw error
            case Right(responseEnvelope) =>
              log.info("Request received: {}", responseEnvelope.responseType)
              responseEnvelope
          }
      }

  private lazy val webSocketSink: Sink[ResponseEnvelope, Future[Done]] =
    Sink.foreach[ResponseEnvelope] {
      requestEnvelope =>
        requestEnvelope.responseType match {
          case ResponseType.None => ???
          case ResponseType.NewPlayerJoined => ???
          case ResponseType.GameJoined =>
            log.info("HERE")
          case ResponseType.StartGameRequested => ???
          case ResponseType.InitiatingToss => ???
          case ResponseType.TossResult => ???
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
        .map(command => TextMessage(command.asInstanceOf[RequestEnvelope].asJson.noSpaces))
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
    eventualWebSocketUpgradeResponse
      .recover {
        case _ =>
          log.warning("Unable to connect.")
          system.terminate()
          System.exit(-1)
      }
    ()
  }

  private def showUI(): Unit = {
    stage = new application.JFXApp.PrimaryStage {
      title = "UNO"

      private val screen: Screen = Screen.primary
      private val bounds: Rectangle2D = screen.visualBounds

      x = bounds.getMinX
      y = bounds.getMinY
      width = bounds.getWidth
      height = bounds.getHeight
      maximized = true

      scene = new Scene {
        private val pane = new BorderPane()
        root = pane

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
