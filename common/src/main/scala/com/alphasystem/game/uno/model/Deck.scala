package com.alphasystem.game.uno.model

import scala.util.Random

class Deck(initialCards: List[CardWrapper]) {
  private var _cards = initialCards

  def shuffle(num: Int = 10): Unit = {
    var shuffledCards = cards
    for (_ <- 1 to num) {
      shuffledCards = Random.shuffle(shuffledCards)
    }
    _cards = shuffledCards
  }

  def draw(num: Int): List[CardWrapper] = {
    val drawnCards = _cards.take(num)
    _cards = _cards.drop(num)
    drawnCards
  }

  def distributeCards(playerIds: List[Int]): Map[Int, List[CardWrapper]] = {
    shuffle()
    var cards = Map.empty[Int, List[CardWrapper]].withDefaultValue(Nil)
    (1 to MaxCards)
      .foreach {
        _ =>
          playerIds
            .foreach {
              id => cards += id -> (cards(id) ::: draw(1))
            }
      }
    cards
      .map {
        case (id, values) =>
          id -> sort(values)
      }
  }

  def cards: List[CardWrapper] = _cards
}

object Deck {
  def apply(): Deck = Deck(buildDeck())

  def apply(initialCards: List[CardWrapper]): Deck = new Deck(initialCards)
}
