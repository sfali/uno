package com.alphasystem.game.uno

import com.alphasystem.game.uno.model.response.Cards
import com.alphasystem.game.uno.model.{Card, Player}

package object test {

  def createPlayer(id: Int, points: Int = 0): Player = Player(id, s"Player${id + 1}", points)

  def toCards(cards: List[Card], players: Array[Player]): List[Cards] =
    cards
      .zipWithIndex
      .foldLeft(List[Cards]()) {
        case (ls, (card, position)) => ls :+ Cards(Some(players(position).name), card :: Nil)
      }
}
