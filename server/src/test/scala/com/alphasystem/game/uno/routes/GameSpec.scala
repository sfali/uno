package com.alphasystem.game.uno.routes

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.cluster.MemberStatus
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.typed.{Cluster, Join}
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest, WSProbe}
import com.alphasystem.game.uno.model.game.GameStatus
import com.alphasystem.game.uno.model.request.{RequestEnvelope, RequestType}
import com.alphasystem.game.uno.model.response._
import com.alphasystem.game.uno.model.{Event, Player, StateInfo, request}
import com.alphasystem.game.uno.server.Main.Guardian
import com.alphasystem.game.uno.server.actor.GameBehavior
import com.alphasystem.game.uno.test._
import io.circe.syntax._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{Eventually, PatienceConfiguration, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

class GameSpec
  extends AnyWordSpec
    with ScalatestRouteTest
    with ScalaFutures
    with Eventually
    with Matchers
    with BeforeAndAfterAll {

  private implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(15, Seconds),
    interval = Span(500, Millis))
  private implicit val testTimeout: RouteTestTimeout = RouteTestTimeout(10.seconds)

  private val testKit = ActorTestKit("uno")
  private val probe = testKit.createTestProbe[Event]()
  private val players = (0 to 4).map(createPlayer).toArray
  private lazy val gameActorRef = GameBehavior.init(testKit.system)
  private lazy val gameRoute = GameRoute(gameActorRef)

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    testKit.spawn[Nothing](Guardian(), "guardian")
    val system = testKit.system
    Cluster(system).manager ! Join(Cluster(system).selfMember.address)
    eventually(PatienceConfiguration.Timeout(10.seconds)) {
      Cluster(system).selfMember.status should ===(MemberStatus.Up)
    }
  }

  protected override def afterAll(): Unit = {
    testKit.shutdownTestKit()
    super.afterAll()
  }

  "Connect to websocket" in {
    val probe = WSProbe()
    WS(uri(Player(0, "Player1"), 1), probe.flow) ~> gameRoute ~> check {
      isWebSocketUpgrade shouldEqual true
    }
  }

  "Add players" in {
    val wsClients = Array(WSProbe(), WSProbe(), WSProbe(), WSProbe(), WSProbe())
    players.map(_.position).foreach(validateJoinGame(1000, players, wsClients))
  }

  "Start game" in {
    val gameId = 1001
    val clients = Array(WSProbe(), WSProbe(), WSProbe(), WSProbe(), WSProbe())
    val client1 = clients(0)
    val player1 = players(0)
    WS(uri(player1, gameId), client1.flow) ~> gameRoute ~> check {
      validateGameJoined(client1, player1, playersAlreadyJoined(0, players))
      val client2 = clients(1)
      val player2 = players(1)
      WS(uri(player2, gameId), client2.flow) ~> gameRoute ~> check {
        validateGameJoined(client2, player2, playersAlreadyJoined(1, players))
        validatePlayerJoined(client1, 0, player2)
        val client3 = clients(2)
        val player3 = players(2)
        WS(uri(player3, gameId), client3.flow) ~> gameRoute ~> check {
          validateGameJoined(client3, player3, playersAlreadyJoined(2, players))
          validatePlayerJoined(client1, 0, player3)
          validatePlayerJoined(client2, 1, player3)
          val client4 = clients(3)
          val player4 = players(3)
          WS(uri(player4, gameId), client4.flow) ~> gameRoute ~> check {
            validateGameJoined(client4, player4, playersAlreadyJoined(3, players))
            validatePlayerJoined(client1, 0, player4)
            validatePlayerJoined(client2, 1, player4)
            validatePlayerJoined(client3, 2, player4)
            val client5 = clients(4)
            val player5 = players(4)
            WS(uri(player5, gameId), client5.flow) ~> gameRoute ~> check {
              validateGameJoined(client5, player5, playersAlreadyJoined(4, players))
              validatePlayerJoined(client1, 0, player5)
              validatePlayerJoined(client2, 1, player5)
              validatePlayerJoined(client3, 2, player5)
              validatePlayerJoined(client4, 3, player5)
              val text = RequestEnvelope(0, RequestType.StartGame, request.Empty()).asJson.noSpaces
              client1.sendMessage(text)
              client1.expectNoMessage()
              val response = ResponseEnvelope(1, ResponseType.ConfirmationMessage, Message(player1.name,
                MessageCode.CanStartGame))
              client2.expectMessage(response.asJson.noSpaces)
              client3.expectMessage(response.copy(position = 2).asJson.noSpaces)
              client4.expectMessage(response.copy(position = 3).asJson.noSpaces)
              client5.expectMessage(response.copy(position = 4).asJson.noSpaces)
            } // end of player 5
          } // // end of player 4
        } // end of player 3
      } // end of player 2
    } // end of player 1
  }

  "Check game state" in {
    val gameId = 1000
    gameActorRef ! ShardingEnvelope(gameId.toString, GameBehavior.GetState(probe.ref))
    val gameState = probe.receiveMessage().asInstanceOf[StateInfo].state
    gameState.id shouldBe gameId
    gameState.players.toList shouldBe players.toList
    gameState.status shouldBe GameStatus.Initiated
  }

  private def validateJoinGame(gameId: Int,
                               players: Array[Player],
                               wsClients: Array[WSProbe])(position: Int): Unit = {
    val player = players(position)
    val client = wsClients(position)
    WS(uri(player, gameId), client.flow) ~> gameRoute ~> check {
      val otherPLayers = playersAlreadyJoined(position, players)
      validateGameJoined(client, player, otherPLayers)
      otherPLayers.map(_.position)
        .foreach {
          pos => validatePlayerJoined(wsClients(pos), pos, player)
        }
    }
  }

  private def playersAlreadyJoined(position: Int, players: Array[Player]) =
    (0 until position).map(pos => players(pos)).toList

  private def validateGameJoined(client: WSProbe,
                                 player: Player,
                                 otherPlayers: List[Player]): Unit = {
    val responseEnvelope = ResponseEnvelope(player.position, ResponseType.GameJoined, PlayerJoined(player, otherPlayers))
    client.expectMessage(responseEnvelope.asJson.noSpaces)
  }

  private def validatePlayerJoined(client: WSProbe,
                                   position: Int,
                                   player: Player): Unit = {
    val responseEnvelope = ResponseEnvelope(position, ResponseType.NewPlayerJoined, PlayerJoined(player))
    client.expectMessage(responseEnvelope.asJson.noSpaces)
  }

  private def uri(player: Player, id: Int) = Uri(s"/gameId/$id/playerName/${player.name}")
}
