package com.alphasystem.game.uno.routes

import akka.NotUsed
import akka.actor.typed.ActorRef
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Flow, GraphDSL, Merge, Sink, Source}
import akka.stream.typed.scaladsl.{ActorSink, ActorSource}
import akka.stream.{FlowShape, OverflowStrategy}
import com.alphasystem.game.uno.model.request.RequestEnvelope
import com.alphasystem.game.uno.model.{Event, Fail, Finished, ResponseEvent}
import com.alphasystem.game.uno.server.actor.GameBehavior
import io.circe.parser._
import io.circe.syntax._
import org.slf4j.LoggerFactory

class GameRoute private(gameActorRef: ActorRef[ShardingEnvelope[GameBehavior.Command]]) {

  private val log = LoggerFactory.getLogger(classOf[GameRoute])

  private val websocketSource: Source[Event, ActorRef[Event]] =
    ActorSource
      .actorRef[Event](
        completionMatcher = {
          case Finished =>
            log.info("Finished get called")
        },
        failureMatcher = {
          case Fail(ex) => ex
        },
        bufferSize = Int.MaxValue,
        overflowStrategy = OverflowStrategy.fail
      )

  private def websocketFlow(gameId: Int, playerName: String): Flow[Message, Message, Any] =
    Flow.fromGraph(GraphDSL.create(websocketSource) {
      implicit builder =>
        actorRefSource =>
          import GraphDSL.Implicits._

          def init(actorRef: ActorRef[Event]): ShardingEnvelope[GameBehavior.Command] =
            ShardingEnvelope(gameId.toString, GameBehavior.JoinGame(playerName, actorRef))

          val materialization = builder.materializedValue.map(init)
          val merge = builder.add(Merge[ShardingEnvelope[GameBehavior.Command]](2))

          val requestToCommandFlow: FlowShape[Message, ShardingEnvelope[GameBehavior.Command]] =
            builder
              .add(Flow[Message]
                .map {
                  case TextMessage.Strict(s) =>
                    decode[RequestEnvelope](s) match {
                      case Left(error) =>
                        log.error(s"unable to parse message: $s", error)
                        throw error

                      case Right(requestEnvelope) =>
                        log.info("Received message of type: {}", requestEnvelope.requestType)
                        requestEnvelope.toCommand(gameId, playerName)
                    }
                  case message =>
                    val textMessage = message.asTextMessage
                    log.error("Message of type {} are not allowed: {}", message.getClass.getSimpleName, textMessage)
                    throw new RuntimeException("Binary messages are not allowed")
                })

          val eventToResponseFlow: FlowShape[Event, Message] =
            builder.add(
              Flow[Event]
                .map {
                  case ResponseEvent(responseEnvelope) => TextMessage(responseEnvelope.asJson.noSpaces)

                  case event => throw new RuntimeException(s"Invalid event: $event")
                })

          val websocketSink: Sink[ShardingEnvelope[GameBehavior.Command], NotUsed] =
            ActorSink
              .actorRef[ShardingEnvelope[GameBehavior.Command]](
                ref = gameActorRef,
                onCompleteMessage = ShardingEnvelope(gameId.toString, GameBehavior.PlayerLeft(playerName)),
                onFailureMessage = {
                  ex =>
                    log.error("Exception occurred in sink", ex)
                    ShardingEnvelope(gameId.toString, GameBehavior.Fail(playerName, ex))
                })

          materialization ~> merge ~> websocketSink
          requestToCommandFlow ~> merge
          actorRefSource ~> eventToResponseFlow

          FlowShape(requestToCommandFlow.in, eventToResponseFlow.out)
    })

  private val route: Route =
    (get & pathPrefix("gameId" / IntNumber) & path("playerName" / Segment)) {
      (gameId, playerName) => handleWebSocketMessages(websocketFlow(gameId, playerName))

    }
}

object GameRoute {
  def apply(gameActorRef: ActorRef[ShardingEnvelope[GameBehavior.Command]]): Route = new GameRoute(gameActorRef).route
}
