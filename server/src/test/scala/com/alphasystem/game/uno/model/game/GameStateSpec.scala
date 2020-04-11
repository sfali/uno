package com.alphasystem.game.uno.model.game

import com.alphasystem.game.uno.model.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateSpec
  extends AnyWordSpec
    with Matchers {

  private var gameState = GameState(1)

  "Create an empty game state" in {
    gameState.dealerId shouldBe 0
    gameState.currentPlayerId shouldBe 0
    gameState.direction shouldBe PlayDirection.Clockwise
    gameState.status shouldBe GameStatus.Initiated
    gameState.players shouldBe empty
    gameState.numOfPlayer shouldBe 0
  }

  "Add players in the game" in {
    var players = createPlayer(0, owner = true) :: Nil
    gameState = gameState.addPlayer("Player1")
    gameState.players.toList shouldBe players
    gameState.numOfPlayer shouldBe 1

    players = players :+ createPlayer(1)
    gameState = gameState.addPlayer("Player2")
    gameState.players.toList shouldBe players
    gameState.numOfPlayer shouldBe 2

    players = players :+ createPlayer(2)
    gameState = gameState.addPlayer("Player3")
    gameState.players.toList shouldBe players
    gameState.numOfPlayer shouldBe 3

    players = players :+ createPlayer(3)
    gameState = gameState.addPlayer("Player4")
    gameState.players.toList shouldBe players
    gameState.numOfPlayer shouldBe 4

    players = players :+ createPlayer(4)
    gameState = gameState.addPlayer("Player5")
    gameState.players.toList shouldBe players
    gameState.numOfPlayer shouldBe 5

    gameState.dealerId shouldBe 0
    gameState.currentPlayerId shouldBe 0
    gameState.direction shouldBe PlayDirection.Clockwise
  }

  "Update dealer will set current player" in {
    gameState = gameState.updateDealer(3)
    gameState.numOfPlayer shouldBe 5
    gameState.dealerId shouldBe 3
    gameState.currentPlayerId shouldBe 4
    gameState.direction shouldBe PlayDirection.Clockwise
  }

  "Update game status" in {
    gameState = gameState.updateStatus(GameStatus.Started)
    gameState.status shouldBe GameStatus.Started
  }

  "Game will move the player in the direction of play" in {
    gameState = gameState.movePlayer
    gameState.currentPlayerId shouldBe 0

    gameState = gameState.movePlayer
    gameState.currentPlayerId shouldBe 1

    gameState = gameState.reverse
    gameState.currentPlayerId shouldBe 0

    gameState = gameState.movePlayer
    gameState.currentPlayerId shouldBe 4

    gameState = gameState.movePlayer
    gameState.currentPlayerId shouldBe 3

    gameState = gameState.skip(2)
    gameState.currentPlayerId shouldBe 0

    gameState = gameState.skip(3)
    gameState.currentPlayerId shouldBe 1

    gameState = gameState.reverse
    gameState.currentPlayerId shouldBe 2

    gameState = gameState.skip(1)
    gameState.currentPlayerId shouldBe 4
  }

  "Update a player points will move dealer" in {
    gameState = gameState.updatePoint(2, 50)
    gameState.players(2) shouldBe createPlayer(2, 50)
    gameState.direction shouldBe PlayDirection.Clockwise
    gameState.status shouldBe GameStatus.Initiated
    gameState.dealerId shouldBe 4
    gameState.currentPlayerId shouldBe 0
  }

  private def createPlayer(id: Int, points: Int = 0, owner: Boolean = false) =
    Player(id, s"Player${id + 1}", points, owner)

}
