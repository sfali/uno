package com.alphasystem.game.uno.client.ui

import akka.Done
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketUpgradeResponse}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.typed.scaladsl.ActorSource
import com.alphasystem.game.uno.model.request.RequestEnvelope
import com.alphasystem.game.uno.model.response.{ResponseEnvelope, ResponseType}
import io.circe.parser._
import io.circe.syntax._
import javafx.embed.swing.JFXPanel
import scalafx.application.{JFXApp, Platform}
import scalafx.geometry.Rectangle2D
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.layout.BorderPane
import scalafx.stage.Screen

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Client(gameId: Int, playerName: String)
            (implicit system: ActorSystem[_])
  extends JFXApp {

  private lazy val log = system.log

  import system.executionContext

  private var inputSource: ActorRef[RequestEnvelope] = _

  private lazy val webSocketSource: Source[RequestEnvelope, ActorRef[RequestEnvelope]] =
    ActorSource
      .actorRef[RequestEnvelope](
        completionMatcher = {
          case msg => log.warn("Completed: {}", msg)
        },
        failureMatcher = {
          case msg =>
            log.warn("Failed with message: {}", msg)
            throw new IllegalArgumentException("?????")
        },
        bufferSize = Int.MaxValue,
        overflowStrategy = OverflowStrategy.fail
      )

  private lazy val webSocketFlow: Flow[Message, ResponseEnvelope, Future[WebSocketUpgradeResponse]] =
    Http()(system.toClassic)
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
          case ResponseType.GameJoined => ???
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

  def run(): Unit = {
    val wsConnection =
      webSocketSource
        .map(command => TextMessage(command.asJson.noSpaces))
        .viaMat(webSocketFlow)(Keep.both)
        .toMat(webSocketSink)(Keep.both)
        .run()
    inputSource = wsConnection._1._1
    val eventualWebSocketUpgradeResponse: Future[WebSocketUpgradeResponse] = wsConnection._1._2
    eventualWebSocketUpgradeResponse
      .onComplete {
        case Success(_) =>
          log.info("connected to server.")
          new JFXPanel() // This will initialize the JavaFx toolkit
          runLater(showUI())

        case Failure(ex) =>
          log.error("onComplete.Failure", ex)
      }
    eventualWebSocketUpgradeResponse.recover {
      case _ =>
        log.warn("Unable to connect.")
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

  private def runLater[R](op: => R): Unit = Platform.runLater(op)
}

object Client {
  def apply(gameId: Int, playerName: String)
           (implicit system: ActorSystem[_]): Client = new Client(gameId, playerName)(system)
}
