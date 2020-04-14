package com.alphasystem.game.uno.model

import scala.util.Random

class Deck(initialCards: List[Card]) {
  private var _cards = initialCards

  def shuffle(num: Int = 10): Unit = {
    var shuffledCards = cards
    for (_ <- 1 to num) {
      shuffledCards = Random.shuffle(shuffledCards)
    }
    _cards = shuffledCards
  }

  def draw(num: Int): List[Card] = {
    val drawnCards = _cards.take(num)
    _cards = _cards.drop(num)
    drawnCards
  }

  def distributeCards(playerIds: List[Int]): Map[Int, List[Card]] = {
    shuffle(50)
    var cards = Map.empty[Int, List[Card]].withDefaultValue(Nil)
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

  def cards: List[Card] = _cards
}

object Deck {
  def apply(): Deck = Deck(buildDeck())

  def apply(initialCards: List[Card]): Deck = new Deck(initialCards)
}
