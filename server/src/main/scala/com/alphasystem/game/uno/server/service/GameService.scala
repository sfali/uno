package com.alphasystem.game.uno.server.service

import akka.actor.typed.ActorRef
import com.alphasystem.game.uno._
import com.alphasystem.game.uno.model.response._
import com.alphasystem.game.uno.model.{Deck, GameType, Player}
import com.alphasystem.game.uno.server.model.game.{ApprovalStatus, GameState, GameStatus, PlayDirection}
import com.alphasystem.game.uno.server.model.{Event, ResponseEvent}
import org.slf4j.LoggerFactory

class GameService(gameId: Int, deckService: DeckService) {

  private val log = LoggerFactory.getLogger(classOf[GameService])
  private var _state = GameState(gameId)
  private var gameMode: GameType = GameType.Classic
  private var playerToActorRefs = Map.empty[String, ActorRef[Event]]
  private var confirmationApprovals = Map.empty[String, Boolean]
  private var currentDeck: Deck = deckService.create()

  def removePlayer(name: String): Unit = {
    _state.player(name).map(Player(_)) match {
      case Some(player) =>
        _state = _state.removePlayer(name)
        playerToActorRefs -= name
        playerToActorRefs
          .foreach {
            case (_, actorRef) =>
              actorRef ! ResponseEvent(ResponseEnvelope(ResponseType.PlayerLeft, PlayerInfo(player)))
          }
      case None => // do nothing
    }
  }

  def joinGame(name: String, replyTo: ActorRef[Event]): Boolean = {
    log.info("Player '{}' is about to join", name)
    val otherPlayers = _state.players.toList.map(Player(_))
    _state = _state.addPlayer(name)
    val playerDetail = _state.player(_state.numOfPlayer - 1)
    playerToActorRefs += playerDetail.name -> replyTo
    playerToActorRefs
      .foreach {
        case (name, actorRef) =>
          val event =
            if (name == playerDetail.name)
              ResponseEvent(ResponseEnvelope(ResponseType.GameJoined, PlayerInfo(Player(playerDetail), otherPlayers)))
            else
              ResponseEvent(ResponseEnvelope(ResponseType.NewPlayerJoined, PlayerInfo(Player(playerDetail))))
          actorRef ! event
      }
    log.info("Player '{}' is joined", name)
    sendCanStartGameMessage()
    _state.reachedCapacity
  }

  def sendCanStartGameMessage(): Unit = {
    if (_state.numOfPlayer == MinNumberOfPlayers) {
      // game has now at least two players, signal the first player to start game
      val playerName = _state.player(0).name
      playerToActorRefs(playerName) ! ResponseEvent(ResponseEnvelope(ResponseType.CanStartGame, Empty()))
    }
  }

  def startGame(playerName: String, mode: GameType): Unit = {
    log.info("Get request to start game from {}", playerName)
    this.gameMode = mode
    val validPlayer = _state.players.exists(_.name == playerName)
    // for invalid, we do not need to send back any response
    if (validPlayer) {
      _state.players.filterNot(_.name == playerName).map(_.name)
        .foreach {
          name =>
            playerToActorRefs(name) ! ResponseEvent(ResponseEnvelope(ResponseType.StartGameRequested, StartGameRequest(playerName, mode)))
        }
    }
  }

  def startGameApprovals(name: String, approved: Boolean): ApprovalStatus =
    _state.player(name) match {
      case Some(playerDetail) =>
        val s = if (approved) "approved " else "rejected"
        log.info("Start game request is {} by {}", s, name)
        confirmationApprovals += (playerDetail.name -> approved)
        // do not count the player who initiated the start game request
        if (confirmationApprovals.size >= _state.numOfPlayer - 1) {
          val acceptedCount = confirmationApprovals.count(_._2 == true).toDouble
          val approvalPercentage = (acceptedCount / playerToActorRefs.size) * 100
          if (approvalPercentage >= 50.0) ApprovalStatus.Approved else ApprovalStatus.Rejected
        } else ApprovalStatus.Waiting

      case None =>
        // should not happen actor must handled this scenario already
        throw new IllegalArgumentException(s"Unknown player: $name")
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
          (0 until _state.numOfPlayer).toList
        case _ => positions
      }
    log.info("Performing toss: {}", _positions.mkString(","))
    val (cards, winners) = performToss(state, currentDeck, _positions)
    val winningPlayers = winners.map(_state.player).map(_.name)
    playerToActorRefs
      .foreach {
        case (_, actorRef) => actorRef ! ResponseEvent(ResponseEnvelope(ResponseType.TossResult, TossResult(cards, winningPlayers)))
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
        val envelope = ResponseEnvelope(ResponseType.IllegalAccess, Empty())
        playerToActorRefs(player.name) ! ResponseEvent(envelope)
      case None => // do nothing
    }

  def state: GameState = _state
}

object GameService {
  def apply(gameId: Int, deckService: DeckService): GameService = new GameService(gameId, deckService)
}
