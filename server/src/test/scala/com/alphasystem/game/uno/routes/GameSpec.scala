package com.alphasystem.game.uno.routes

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.cluster.MemberStatus
import akka.cluster.sharding.typed.ShardingEnvelope
import akka.cluster.typed.{Cluster, Join}
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest, WSProbe}
import com.alphasystem.game.uno.model.game.GameStatus
import com.alphasystem.game.uno.model.response.{PlayerJoined, ResponseEnvelope, ResponseType}
import com.alphasystem.game.uno.model.{Event, Player, StateInfo}
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

  private val gameId = 1000
  private val testKit = ActorTestKit("uno")
  private val probe = testKit.createTestProbe[Event]()
  private lazy val gameActorRef = GameBehavior.init(testKit.system)
  private val players = (0 to 4).map(createPlayer).toArray
  private val wsProbes = Array(WSProbe(), WSProbe(), WSProbe(), WSProbe(), WSProbe())
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
    WS(uri("Player1", 1), probe.flow) ~> gameRoute ~> check {
      isWebSocketUpgrade shouldEqual true
    }
  }

  "Check game state" in {
    gameActorRef ! ShardingEnvelope(gameId.toString, GameBehavior.GetState(probe.ref))
    val gameState = probe.receiveMessage().asInstanceOf[StateInfo].state
    gameState.id shouldBe gameId
    gameState.players.toList shouldBe empty
    gameState.status shouldBe GameStatus.Initiated
  }

  "Add players" in {
    // Player1 join the game
    val probe1 = wsProbes.head
    val player1 = players.head
    WS(uri(player1.name), probe1.flow) ~> gameRoute ~> check {
      validateGameJoined(probe1, player1)
    }

    // Player2 join the game
    val probe2 = wsProbes(1)
    val player2 = players(1)
    WS(uri(player2.name), probe2.flow) ~> gameRoute ~> check {
      validateGameJoined(probe2, player2, player1 :: Nil)
      validatePlayerJoined(probe1, player1.position, player2)
    }

    // Player3 join the game
    val probe3 = wsProbes(2)
    val player3 = players(2)
    WS(uri(player3.name), probe3.flow) ~> gameRoute ~> check {
      validateGameJoined(probe3, player3, player1 :: player2 :: Nil)
      validatePlayerJoined(probe1, player1.position, player3)
      validatePlayerJoined(probe2, player2.position, player3)
    }


    // Player4 join the game
    val probe4 = wsProbes(3)
    val player4 = players(3)
    WS(uri(player4.name), probe4.flow) ~> gameRoute ~> check {
      validateGameJoined(probe4, player4, player1 :: player2 :: player3 :: Nil)
      validatePlayerJoined(probe1, player1.position, player4)
      validatePlayerJoined(probe2, player2.position, player4)
      validatePlayerJoined(probe3, player3.position, player4)
    }

    // Player5 join the game
    val probe5 = wsProbes(4)
    val player5 = players(4)
    WS(uri(player5.name), probe5.flow) ~> gameRoute ~> check {
      validateGameJoined(probe5, player5, player1 :: player2 :: player3 :: player4 :: Nil)
      validatePlayerJoined(probe1, player1.position, player5)
      validatePlayerJoined(probe2, player2.position, player5)
      validatePlayerJoined(probe3, player3.position, player5)
      validatePlayerJoined(probe4, player4.position, player5)
    }
  }

  private def validateGameJoined(probe: WSProbe,
                                 player: Player,
                                 otherPlayers: List[Player] = Nil): Unit = {
    val responseEnvelope = ResponseEnvelope(player.position, ResponseType.GameJoined, PlayerJoined(player, otherPlayers))
    probe.expectMessage(responseEnvelope.asJson.noSpaces)
  }

  private def validatePlayerJoined(probe: WSProbe,
                                   position: Int,
                                   player: Player): Unit = {
    val responseEnvelope = ResponseEnvelope(position, ResponseType.NewPlayerJoined, PlayerJoined(player))
    probe.expectMessage(responseEnvelope.asJson.noSpaces)
  }

  private def uri(playerName: String, id: Int = this.gameId) = Uri(s"/gameId/$id/playerName/$playerName")
}
