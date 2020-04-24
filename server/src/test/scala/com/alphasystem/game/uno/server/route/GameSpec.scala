package com.alphasystem.game.uno.server.route

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.cluster.MemberStatus
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.typed.{Cluster, Join}
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest, WSProbe}
import com.alphasystem.game.uno.model._
import com.alphasystem.game.uno.model.request.{RequestEnvelope, RequestType}
import com.alphasystem.game.uno.model.response._
import com.alphasystem.game.uno.server.Main.Guardian
import com.alphasystem.game.uno.server.actor.GameBehavior
import com.alphasystem.game.uno.server.model.game.{GameState, GameStatus}
import com.alphasystem.game.uno.server.model.{Event, StateInfo}
import com.alphasystem.game.uno.server.service.FileBasedDeckService
import com.alphasystem.game.uno.test._
import io.circe.generic.auto._
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
  private val deckService: FileBasedDeckService = FileBasedDeckService()

  private val testKit = ActorTestKit("uno")
  private val probe = testKit.createTestProbe[Event]()
  private val players = (0 to 4).map(pos => Player(createPlayer(pos))).toArray
  private lazy val gameActorRef = GameBehavior.init(testKit.system, deckService)
  private lazy val gameRoute = GameRoute(gameActorRef)

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    testKit.spawn[Nothing](Guardian(deckService, liveMode = false), "guardian")
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
    WS(uri(Player("Player1"), 1), probe.flow) ~> gameRoute ~> check {
      isWebSocketUpgrade shouldEqual true
    }
  }

  "Add players" in {
    val wsClients = Array(WSProbe(), WSProbe(), WSProbe(), WSProbe(), WSProbe())
    players.zipWithIndex.map(_._2).foreach(validateJoinGame(1000, players, wsClients))
  }

  "Start game" in {
    val gameId = 1001
    val expectedGameState =
      players.foldLeft(GameState(gameId)) {
        (state, player) => state.addPlayer(player.name)
      }
    val clients = Array(WSProbe(), WSProbe(), WSProbe(), WSProbe(), WSProbe())
    WS(uri(players(0), gameId), clients(0).flow) ~> gameRoute ~> check {
      validateJoinGame(0, clients)
      WS(uri(players(1), gameId), clients(1).flow) ~> gameRoute ~> check {
        validateJoinGame(1, clients)
        WS(uri(players(2), gameId), clients(2).flow) ~> gameRoute ~> check {
          validateJoinGame(2, clients)
          WS(uri(players(3), gameId), clients(3).flow) ~> gameRoute ~> check {
            validateJoinGame(3, clients)
            WS(uri(players(4), gameId), clients(4).flow) ~> gameRoute ~> check {
              validateJoinGame(4, clients)
              validateStartGame(clients)
              validateStartGameConfirmation(clients)
              validatePerformToss(clients)
              validateGameState(gameId, expectedGameState.updateStatus(GameStatus.Started))
              clients.foreach(_.sendCompletion())
            } // end of player 5
          } // // end of player 4
        } // end of player 3
      } // end of player 2
    } // end of player 1
  }

  "Check game state" in {
    val gameId = 1000
    val gameState = getGameState(gameId)
    gameState.id shouldBe gameId
    gameState.players.toList.map(Player(_)) shouldBe players.toList
    gameState.status shouldBe GameStatus.Initiated
  }

  private def getGameState(gameId: Int) = {
    gameActorRef ! ShardingEnvelope(gameId.toString, GameBehavior.GetState(probe.ref))
    probe.receiveMessage().asInstanceOf[StateInfo].state
  }

  private def validateGameState(gameId: Int, expected: GameState) = {
    val gameState = getGameState(gameId)
    gameState.id shouldBe expected.id
    gameState.status shouldBe expected.status
  }

  private def validateJoinGame(position: Int, clients: Array[WSProbe]): Unit = {
    validateGameJoined(clients(position), players(position), playersAlreadyJoined(position, players))
    (0 until position).foreach(pos => validatePlayerJoined(clients(pos), players(position)))
  }

  private def validateStartGame(clients: Array[WSProbe]): Unit = {
    val client = clients(0)
    val text = RequestEnvelope(RequestType.StartGame, request.Empty()).asJson.noSpaces
    client.sendMessage(text)
    client.expectNoMessage()
    (1 until clients.length)
      .foreach {
        pos =>
          val response = ResponseEnvelope(ResponseType.StartGameRequested, Empty())
          clients(pos).expectMessage(response.asJson.noSpaces)
      }
  }

  private def validateStartGameConfirmation(clients: Array[WSProbe]): Unit = {
    clients(2).sendMessage(RequestEnvelope(RequestType.StartGameRejected, request.Empty()).asJson.noSpaces)
    val requestEnvelope = RequestEnvelope(RequestType.StartGameApproved, request.Empty())
    clients(1).sendMessage(requestEnvelope.asJson.noSpaces)
    clients(3).sendMessage(requestEnvelope.asJson.noSpaces)
    clients(4).sendMessage(requestEnvelope.asJson.noSpaces)
    val text = ResponseEnvelope(ResponseType.InitiatingToss, Empty()).asJson.noSpaces
    clients.foreach(client => client.expectMessage(text))
  }

  private def validatePerformToss(clients: Array[WSProbe]): Unit = {
    var tossResult = TossResult(
      Cards(Some("Player1"), Card(Color.Blue, CardEntry.Five) :: Nil) ::
        Cards(Some("Player2"), Card(Color.Red, CardEntry.Four) :: Nil) ::
        Cards(Some("Player3"), Card(Color.Red, CardEntry.Eight) :: Nil) ::
        Cards(Some("Player4"), Card(Color.Yellow, CardEntry.Two) :: Nil) ::
        Cards(Some("Player5"), Card(Color.Yellow, CardEntry.Eight) :: Nil) ::
        Nil)
    var responseEnvelope = ResponseEnvelope(ResponseType.TossResult, tossResult)
    clients.foreach(client => client.expectNoMessage(3.seconds))
    clients.foreach(client => client.expectMessage(responseEnvelope.asJson.noSpaces))

    tossResult = TossResult(
      Cards(Some("Player3"), Card(Color.Yellow, CardEntry.Six) :: Nil) ::
        Cards(Some("Player5"), Card(Color.Green, CardEntry.Six) :: Nil) ::
        Nil)
    responseEnvelope = ResponseEnvelope(ResponseType.TossResult, tossResult)
    clients.foreach(client => client.expectNoMessage(3.seconds))
    clients(2).expectMessage(responseEnvelope.asJson.noSpaces)
    clients(4).expectMessage(responseEnvelope.asJson.noSpaces)

    tossResult = TossResult(
      Cards(Some("Player3"), Card(Color.Green, CardEntry.Two) :: Nil) ::
        Cards(Some("Player5"), Card(Color.Yellow, CardEntry.DrawTwo) :: Nil) ::
        Nil)
    responseEnvelope = ResponseEnvelope(ResponseType.TossResult, tossResult)
    clients.foreach(client => client.expectNoMessage(3.seconds))
    clients(2).expectMessage(responseEnvelope.asJson.noSpaces)
    clients(4).expectMessage(responseEnvelope.asJson.noSpaces)
  }

  private def validateJoinGame(gameId: Int,
                               players: Array[Player],
                               wsClients: Array[WSProbe])(position: Int): Unit = {
    val player = players(position)
    val client = wsClients(position)
    WS(uri(player, gameId), client.flow) ~> gameRoute ~> check {
      val otherPLayers = playersAlreadyJoined(position, players)
      validateGameJoined(client, player, otherPLayers)
      otherPLayers.zipWithIndex.map(_._2)
        .foreach {
          pos => validatePlayerJoined(wsClients(pos), player)
        }
    }
  }

  private def playersAlreadyJoined(position: Int, players: Array[Player]) =
    (0 until position).map(pos => players(pos)).toList

  private def validateGameJoined(client: WSProbe,
                                 player: Player,
                                 otherPlayers: List[Player]): Unit = {
    val responseEnvelope = ResponseEnvelope(ResponseType.GameJoined, PlayerInfo(player, otherPlayers))
    client.expectMessage(responseEnvelope.asJson.noSpaces)
  }

  private def validatePlayerJoined(client: WSProbe,
                                   player: Player): Unit = {
    val responseEnvelope = ResponseEnvelope(ResponseType.NewPlayerJoined, PlayerInfo(player))
    client.expectMessage(responseEnvelope.asJson.noSpaces)
  }

  private def uri(player: Player, id: Int) = Uri(s"/gameId/$id/playerName/${player.name}")
}
