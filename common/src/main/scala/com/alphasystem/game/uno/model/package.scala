package com.alphasystem.game.uno

package object model {

  val MaxCards: Int = 7

  private[model] def buildDeck(): List[Card] =
    Color
      .values
      .foldLeft(List[Card]()) {
        (ls, color) =>
          val cards =
            (color.firstSuite.toList ::: color.secondSuite.toList)
              .map {
                card => Card(color, card)
              }
          ls ::: cards
      }

  private[model] def sort(cards: List[Card]): List[Card] =
    cards
      .sortWith {
        case (wrapper1, wrapper2) =>
          val color1 = wrapper1.color
          val color2 = wrapper2.color
          (color1.value == color2.value && wrapper1.card.value < wrapper2.card.value) || color1.value < color2.value
      }

}
