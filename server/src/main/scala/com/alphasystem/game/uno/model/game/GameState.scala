package com.alphasystem.game.uno.model.game

import com.alphasystem.game.uno._
import com.alphasystem.game.uno.model.Player

case class GameState(id: Int,
                     direction: PlayDirection,
                     status: GameStatus,
                     players: Array[Player],
                     dealerId: Int,
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

  def reverse: GameState = {
    val direction = this.direction.reverse
    copy(direction = direction, currentPlayerId = nextPlayer(currentPlayerId, numOfPlayer, direction))
  }

  def skip(num: Int): GameState = {
    val currentPlayer =
      direction match {
        case PlayDirection.Clockwise => this.currentPlayerId + num
        case PlayDirection.CounterClockwise => this.currentPlayerId - num
      }
    copy(currentPlayerId = nextPlayer(currentPlayer, numOfPlayer, direction))
  }

  def updateDealer(dealerId: Int): GameState =
    copy(direction = DefaultPlayDirection, dealerId = dealerId, currentPlayerId = nextPlayer(dealerId, numOfPlayer))

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

  def movePlayer: GameState = copy(currentPlayerId = nextPlayer(currentPlayerId, numOfPlayer, direction))

}

object GameState {
  def apply(id: Int,
            direction: PlayDirection,
            status: GameStatus,
            players: Array[Player],
            dealerId: Int,
            currentPlayer: Int): GameState = new GameState(id, direction, status, players, dealerId, currentPlayer)

  def apply(id: Int,
            direction: PlayDirection,
            status: GameStatus,
            players: Array[Player],
            dealerId: Int): GameState =
    GameState(id, direction, status, players, dealerId, nextPlayer(dealerId, players.length, direction))

  def apply(id: Int): GameState = GameState(id, DefaultPlayDirection, GameStatus.Initiated, Array.empty, 0, 0)
}
