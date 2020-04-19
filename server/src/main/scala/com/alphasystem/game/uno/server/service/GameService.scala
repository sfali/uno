package com.alphasystem.game.uno.server.service

import akka.actor.typed.ActorRef
import com.alphasystem.game.uno.server.model.game.{GameState, GameStatus, PlayDirection}
import com.alphasystem.game.uno.model.response._
import com.alphasystem.game.uno.model.{Deck, Event, ResponseEvent}
import org.slf4j.LoggerFactory
import com.alphasystem.game.uno._

class GameService(gameId: Int, deckService: DeckService) {

  private val log = LoggerFactory.getLogger(classOf[GameService])
  private var _state = GameState(gameId)
  private var playerToActorRefs = Map.empty[Int, ActorRef[Event]]
  private var confirmationApprovals = Map.empty[Int, Boolean].withDefaultValue(false)
  private var currentDeck: Deck = _

  def joinGame(name: String, replyTo: ActorRef[Event]): Boolean = {
    log.info("Player '{}' is about to join", name)
    val otherPlayers = _state.players.toList
    _state = _state.addPlayer(name)
    val player = _state.player(_state.numOfPlayer - 1)
    playerToActorRefs += player.position -> replyTo
    playerToActorRefs
      .foreach {
        case (position, actorRef) =>
          val event =
            if (position == player.position)
              ResponseEvent(ResponseEnvelope(ResponseType.GameJoined, PlayerJoined(player, otherPlayers)))
            else
              ResponseEvent(ResponseEnvelope(ResponseType.NewPlayerJoined, PlayerJoined(player)))
          actorRef ! event
      }
    _state.reachedCapacity
  }

  def startGame(name: String): Unit = {
    log.info("Get request to start game from {}", name)
    val validPlayer = _state.players.exists(_.name == name)
    // for invalid, we do not need to send back any response
    if (validPlayer) {
      _state.players.filterNot(_.name == name).map(_.position)
        .foreach {
          position =>
            playerToActorRefs(position) ! ResponseEvent(ResponseEnvelope(ResponseType.StartGameRequested, Empty()))
        }
    }
  }

  def startGameApprovals(name: String, approved: Boolean): Boolean = {
    val position = _state.position(name).getOrElse(-1)
    if (position >= 0) {
      val s = if (approved) "approved " else "rejected"
      log.info("Start game request is {} by {}", s, name)
      confirmationApprovals += (position -> approved)
      val acceptedCount = confirmationApprovals.count(_._2 == true).toDouble
      val approvalPercentage = (acceptedCount / playerToActorRefs.size) * 100
      approvalPercentage >= 50.0
    } else false
  }

  def notifyGameStart(): Unit = {
    log.info("Updating game status to start")
    _state = _state.updateStatus(GameStatus.TossInitiated)
    playerToActorRefs
      .foreach {
        case (_, actorRef) =>
          actorRef ! ResponseEvent(ResponseEnvelope(ResponseType.InitiatingToss, Empty()))
      }
  }

  def toss(positions: List[Int]): List[Int] = {
    val _positions =
      positions match {
        case Nil =>
          currentDeck = deckService.create()
          (0 until _state.numOfPlayer).toList
        case _ => positions
      }
    log.info("Performing toss: {}", _positions.mkString(","))
    val (tossResult, winners) = performToss(state, currentDeck, _positions)
    playerToActorRefs
      .foreach {
        case (_, actorRef) => actorRef ! ResponseEvent(ResponseEnvelope(ResponseType.TossResult, tossResult))
      }
    if (winners.length == 1) {
      // winner is the player who would start the round, we need to find the dealer, who would be the player
      // right to the winner
      val dealer = nextPlayer(winners.head, _state.numOfPlayer, PlayDirection.CounterClockwise)
      _state = _state.updateDealer(dealer).updateStatus(GameStatus.Started)
      currentDeck = deckService.create()
    }
    winners
  }

  def illegalAccess(name: String): Unit =
    _state.player(name) match {
      case Some(player) =>
        val position = player.position
        val envelope = ResponseEnvelope(ResponseType.IllegalAccess, Empty())
        playerToActorRefs(position) ! ResponseEvent(envelope)
      case None => // do nothing
    }

  def state: GameState = _state
}

object GameService {
  def apply(gameId: Int, deckService: DeckService): GameService = new GameService(gameId, deckService)
}
