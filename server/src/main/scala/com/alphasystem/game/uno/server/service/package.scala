package com.alphasystem.game.uno.server

import com.alphasystem.game.uno.model.game.GameState
import com.alphasystem.game.uno.model.response.{Cards, TossResult}
import com.alphasystem.game.uno.model.{Card, CardEntry, Deck}

package object service {

  /**
   * Draw cards for toss (to determine who would start game).
   *
   * The function will draw one card for each player (based on parameter positions), create the payload for each player,
   * find the highest value card, find whether there are multiple winners.
   *
   * For initial call positions for all players will be passed on the function argument, on subsequent calls will made
   * if there are multiple winners.
   *
   * @param state     current game state
   * @param deck      current deck
   * @param positions index of the players
   * @return a tuple, where first element contains [[TossResult]], and second
   *         element containing List of player position with highest card
   */
  def performToss(state: GameState,
                  deck: Deck,
                  positions: List[Int]): (TossResult, List[Int]) = {
    // draw cards
    val drawnCards =
      positions
        .map {
          position => (position, deck.draw(1).head)
        }.toMap

    // payload
    val cards =
      drawnCards
        .foldLeft(List[Cards]()) {
          case (ls, (position, card)) =>
            ls :+ Cards(Some(state.player(position).name), card :: Nil)
        }

    // max and duplicates
    val (position, maxCard) = highestValueCard(drawnCards)
    val duplicates = findDuplicates(drawnCards)

    (TossResult(cards), duplicates.getOrElse(maxCard.card, position :: Nil))
  }

  /* Card with highest value in the drawn cards */
  private def highestValueCard(cards: Map[Int, Card]): (Int, Card) = cards.toList.maxBy(_._2.card)

  /* Find any duplicates in the drawn cards */
  private def findDuplicates(cards: Map[Int, Card]): Map[CardEntry, List[Int]] =
    cards
      .toList
      .groupBy(_._2.card)
      .collect { case (entry, ls@List(_, _, _*)) => (entry, ls.map(_._1)) }
}
