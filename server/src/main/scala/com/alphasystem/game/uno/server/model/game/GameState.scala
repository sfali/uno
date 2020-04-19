package com.alphasystem.game.uno.server.model.game

import com.alphasystem.game.uno._
import com.alphasystem.game.uno.model.Player

case class GameState(id: Int,
                     direction: PlayDirection,
                     status: GameStatus,
                     players: Array[Player],
                     dealerId: Int,
                     previousPlayerId: Int,
                     currentPlayerId: Int) {

  val numOfPlayer: Int = players.length

  def reachedCapacity: Boolean = numOfPlayer >= MaxNumberOfPlayers

  def hasMinimumCapacity: Boolean = numOfPlayer >= MinNumberOfPlayers

  def addPlayer(name: String): GameState = copy(players = players :+ Player(numOfPlayer, name))

  def player(id: Int): Player = players(id)

  def player(name: String): Option[Player] = players.find(_.name == name)

  def position(name: String): Option[Int] = player(name).map(_.position)

  def currentPlayer: Player = players(currentPlayerId)

  def updateStatus(status: GameStatus): GameState = copy(status = status)

  def movePlayer: GameState = copy(previousPlayerId = currentPlayerId,
    currentPlayerId = nextPlayer(currentPlayerId, numOfPlayer, direction))

  def reverse: GameState = {
    val direction = this.direction.reverse
    copy(direction = direction, previousPlayerId = currentPlayerId,
      currentPlayerId = nextPlayer(currentPlayerId, numOfPlayer, direction))
  }

  def skip(num: Int): GameState = {
    val currentPlayerId =
      direction match {
        case PlayDirection.Clockwise => this.currentPlayerId + num
        case PlayDirection.CounterClockwise => this.currentPlayerId - num
      }
    copy(previousPlayerId = this.currentPlayerId, currentPlayerId = nextPlayer(currentPlayerId, numOfPlayer, direction))
  }

  def activateCurrentPlayer: GameState = copy(currentPlayerId = nextPlayer(previousPlayerId, numOfPlayer, direction))

  def resetCurrentPlayer: GameState = copy(currentPlayerId = -1)

  def updateDealer(dealerId: Int): GameState =
    copy(direction = DefaultPlayDirection, dealerId = dealerId, previousPlayerId = dealerId, currentPlayerId = -1)

  def updatePoint(playerId: Int, points: Int): GameState = {
    var updatedPlayer = this.players(playerId)
    updatedPlayer = updatedPlayer.copy(points = updatedPlayer.points + points)
    val players =
      this.players.zipWithIndex
        .map {
          case (player, index) =>
            if (index == playerId) updatedPlayer
            else player
        }
    val dealerId = nextPlayer(this.dealerId, numOfPlayer)
    copy(direction = DefaultPlayDirection, status = GameStatus.Initiated, players = players, dealerId = dealerId,
      currentPlayerId = nextPlayer(dealerId, numOfPlayer))
  }

}

object GameState {
  def apply(id: Int,
            direction: PlayDirection,
            status: GameStatus,
            players: Array[Player],
            dealerId: Int,
            previousPlayer: Int,
            currentPlayer: Int): GameState =
    new GameState(id, direction, status, players, dealerId, previousPlayer, currentPlayer)

  def apply(id: Int,
            direction: PlayDirection,
            status: GameStatus,
            players: Array[Player],
            dealerId: Int): GameState =
    GameState(id, direction, status, players, dealerId, dealerId, -1)

  def apply(id: Int): GameState = GameState(id, DefaultPlayDirection, GameStatus.Initiated, Array.empty, 0, 0, -1)
}
