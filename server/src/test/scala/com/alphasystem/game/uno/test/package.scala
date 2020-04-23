package com.alphasystem.game.uno

import com.alphasystem.game.uno.model.response.Cards
import com.alphasystem.game.uno.model.{Card, PlayerDetail}

package object test {

  def createPlayer(id: Int, points: Int = 0): PlayerDetail = PlayerDetail(s"Player${id + 1}", points = points)

  def toCards(cards: List[Card], players: Array[PlayerDetail]): List[Cards] =
    cards
      .zipWithIndex
      .foldLeft(List[Cards]()) {
        case (ls, (card, position)) => ls :+ Cards(Some(players(position).name), card :: Nil)
      }
}
