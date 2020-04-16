package com.alphasystem.game.uno.server.service

import akka.actor.typed.ActorRef
import com.alphasystem.game.uno.model.{Event, ResponseEvent}
import com.alphasystem.game.uno.model.game.GameState
import com.alphasystem.game.uno.model.response.{Message, MessageCode, PlayerJoined, ResponseEnvelope, ResponseType}
import org.slf4j.LoggerFactory

class GameService(gameId: Int) {

  private val log = LoggerFactory.getLogger(classOf[GameService])
  private var _state = GameState(gameId)
  private var playerToActorRefs = Map.empty[Int, ActorRef[Event]]

  def joinGame(name: String, replyTo: ActorRef[Event]): Boolean = {
    log.info("Player '{}' is about to join", name)
    val otherPlayers = _state.players.toList
    _state = _state.addPlayer(name)
    val player = _state.player(_state.numOfPlayer - 1)
    playerToActorRefs += player.position -> replyTo
    playerToActorRefs
      .foreach {
        case (id, actorRef) =>
          val event =
            if (id == player.position)
              ResponseEvent(ResponseEnvelope(id, ResponseType.GameJoined, PlayerJoined(player, otherPlayers)))
            else
              ResponseEvent(ResponseEnvelope(id, ResponseType.NewPlayerJoined, PlayerJoined(player)))
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
            val envelope = ResponseEnvelope(position, ResponseType.ConfirmationMessage, Message(name,
              MessageCode.CanStartGame))
            playerToActorRefs(position) ! ResponseEvent(envelope)
        }
    }
  }

  def illegalMove(name: String): Unit =
    _state.player(name) match {
      case Some(player) =>
        val position = player.position
        val envelope = ResponseEnvelope(position, ResponseType.ErrorMessage, Message(name, MessageCode.IllegalMove))
        playerToActorRefs(position) ! ResponseEvent(envelope)
      case None => // do nothing
    }

  def state: GameState = _state
}

object GameService {
  def apply(gameId: Int): GameService = new GameService(gameId)
}
