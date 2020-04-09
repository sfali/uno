package com.alphasystem.game.uno.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ModelSpec
  extends AnyWordSpec
    with Matchers {

  "Creates suites properly" in {
    val expectedFirstSuite = Card.Zero :: Card.One :: Card.Two :: Card.Three :: Card.Four :: Card.Five :: Card.Six ::
      Card.Seven :: Card.Eight :: Card.Nine :: Card.Skip :: Card.Reverse :: Card.DrawTwo :: Card.Wild :: Nil
    val expectedSecondSuite = Card.One :: Card.Two :: Card.Three :: Card.Four :: Card.Five :: Card.Six ::
      Card.Seven :: Card.Eight :: Card.Nine :: Card.Skip :: Card.Reverse :: Card.DrawTwo :: Card.WildDrawFour :: Nil

    Color.values.foreach {
      color =>
        color.firstSuite.toList shouldBe expectedFirstSuite
        color.secondSuite.toList shouldBe expectedSecondSuite
    }

    val totalCards =
      Color.values.foldLeft(0) {
        (total, color) =>
          total + color.firstSuite.length + color.secondSuite.length
      }
    totalCards shouldBe 108
  }

  "Create Deck properly" in {
    Deck().cards.toSet.size shouldBe 108
  }

  "Distribute cards properly" in {
    val playerIds = (1 to 3).toList
    val deck = Deck()
    val cards = deck.distributeCards(playerIds)
    cards.foreach {
      case (i, value) => println(s"$i: $value")
    }
    val allCards =
      cards.values.foldLeft(List[CardWrapper]()) {
        (aggregatedList, ls) => aggregatedList ::: ls

      }.toSet
    allCards.size shouldBe 21
  }
}
