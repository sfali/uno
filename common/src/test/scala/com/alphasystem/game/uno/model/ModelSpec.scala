package com.alphasystem.game.uno.model

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ModelSpec
  extends AnyWordSpec
    with Matchers {

  "Creates suites properly" in {
    val expectedFirstSuite = CardEntry.Zero :: CardEntry.One :: CardEntry.Two :: CardEntry.Three :: CardEntry.Four :: CardEntry.Five :: CardEntry.Six ::
      CardEntry.Seven :: CardEntry.Eight :: CardEntry.Nine :: CardEntry.Skip :: CardEntry.Reverse :: CardEntry.DrawTwo :: Nil
    val expectedSecondSuite = CardEntry.One :: CardEntry.Two :: CardEntry.Three :: CardEntry.Four :: CardEntry.Five :: CardEntry.Six ::
      CardEntry.Seven :: CardEntry.Eight :: CardEntry.Nine :: CardEntry.Skip :: CardEntry.Reverse :: CardEntry.DrawTwo :: Nil

    Color.values.dropRight(1).foreach {
      color =>
        color.firstSuite.toList shouldBe expectedFirstSuite
        color.secondSuite.toList shouldBe expectedSecondSuite
    }

    val symbol = Color.values.last
    symbol.firstSuite.toList shouldBe CardEntry.Wild :: CardEntry.Wild :: CardEntry.Wild :: CardEntry.Wild :: Nil
    symbol.secondSuite.toList shouldBe CardEntry.WildDrawFour :: CardEntry.WildDrawFour :: CardEntry.WildDrawFour :: CardEntry.WildDrawFour :: Nil

    val totalCards =
      Color.values.foldLeft(0) {
        (total, color) =>
          total + color.firstSuite.length + color.secondSuite.length
      }
    totalCards shouldBe 108
  }

  "Create Deck properly" in {
    Deck().cards.size shouldBe 108
  }

  "Distribute cards properly" in {
    val playerIds = (1 to 3).toList
    val deck = Deck()
    val cards = deck.distributeCards(playerIds)
    cards.foreach {
      case (i, value) => println(s"$i: $value")
    }
    val allCards =
      cards.values.foldLeft(List[Card]()) {
        (aggregatedList, ls) => aggregatedList ::: ls

      }
    allCards.size shouldBe 21
  }
}
