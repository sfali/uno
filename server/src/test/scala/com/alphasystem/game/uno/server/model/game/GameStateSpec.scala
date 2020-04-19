package com.alphasystem.game.uno.server.model.game

import com.alphasystem.game.uno.model.response.TossResult
import com.alphasystem.game.uno.model.{Card, CardEntry, Color, Deck}
import com.alphasystem.game.uno.server.service._
import com.alphasystem.game.uno.test._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class GameStateSpec
  extends AnyWordSpec
    with Matchers {

  private var gameState = GameState(1)

  "Create an empty game state" in {
    gameState.dealerId shouldBe 0
    gameState.previousPlayerId shouldBe 0
    gameState.currentPlayerId shouldBe -1
    gameState.direction shouldBe PlayDirection.Clockwise
    gameState.status shouldBe GameStatus.Initiated
    gameState.players shouldBe empty
    gameState.numOfPlayer shouldBe 0
  }

  "Add players in the game" in {
    var players = createPlayer(0) :: Nil
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
    gameState.previousPlayerId shouldBe 0
    gameState.currentPlayerId shouldBe -1
    gameState.direction shouldBe PlayDirection.Clockwise
  }

  "Update dealer will set current player" in {
    gameState = gameState.updateDealer(3).activateCurrentPlayer
    gameState.numOfPlayer shouldBe 5
    gameState.dealerId shouldBe 3
    gameState.previousPlayerId shouldBe 3
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

  "Reset and activate player" in {
    gameState = gameState.resetCurrentPlayer
    gameState.previousPlayerId shouldBe 2
    gameState.currentPlayerId shouldBe -1

    gameState = gameState.activateCurrentPlayer
    gameState.previousPlayerId shouldBe 2
    gameState.currentPlayerId shouldBe 3

    gameState = gameState.movePlayer
    gameState.previousPlayerId shouldBe 3
    gameState.currentPlayerId shouldBe 4

    gameState = gameState.skip(1)
    gameState.previousPlayerId shouldBe 4
    gameState.currentPlayerId shouldBe 1

    gameState = gameState.reverse
    gameState.previousPlayerId shouldBe 1
    gameState.currentPlayerId shouldBe 0
  }

  "Update a player points will move dealer" in {
    gameState = gameState.updatePoint(2, 50)
    gameState.players(2) shouldBe createPlayer(2, 50)
    gameState.direction shouldBe PlayDirection.Clockwise
    gameState.status shouldBe GameStatus.Initiated
    gameState.dealerId shouldBe 4
    gameState.currentPlayerId shouldBe 0
  }

  "Perform toss with a unique winner" in {
    val initialCards = Card(Color.Blue, CardEntry.Five) :: Card(Color.Yellow, CardEntry.Skip) ::
      Card(Color.Green, CardEntry.Nine) :: Card(Color.Red, CardEntry.Eight) :: Card(Color.Green, CardEntry.Five) :: Nil
    val (tossResult, winners) = performToss(gameState, Deck(initialCards), gameState.players.zipWithIndex.map(_._2).toList)
    winners shouldBe 1 :: Nil
    tossResult shouldBe TossResult(toCards(initialCards, gameState.players))
  }

  "Perform toss with a multiple winners" in {
    val initialCards = Card(Color.Blue, CardEntry.Skip) :: Card(Color.Yellow, CardEntry.Skip) ::
      Card(Color.Green, CardEntry.Nine) :: Card(Color.Red, CardEntry.Eight) :: Card(Color.Green, CardEntry.Five) ::
      Card(Color.Symbol, CardEntry.Reverse) :: Card(Color.Yellow, CardEntry.Five) :: Nil
    val deck = Deck(initialCards)
    val (tossResult, winners) = performToss(gameState, deck, gameState.players.zipWithIndex.map(_._2).toList)
    val expectedWinners = 0 :: 1 :: Nil
    winners shouldBe expectedWinners
    tossResult shouldBe TossResult(toCards(initialCards.dropRight(2), gameState.players))

    val (tossResult1, winners1) = performToss(gameState, deck, expectedWinners)
    winners1 shouldBe 0 :: Nil
    tossResult1 shouldBe TossResult(toCards(initialCards.takeRight(2), gameState.players))
  }
}
