package com.alphasystem.game.uno

package object model {

  val MaxCards: Int = 7

  private[model] def buildDeck(): List[CardWrapper] = {
    val allCards =
      Color
        .values
        .foldLeft(List[CardWrapper]()) {
          (ls, color) =>
            val cards =
              (color.firstSuite.toList ::: color.secondSuite.toList)
                .map {
                  card => CardWrapper(0, color, card)
                }
            ls ::: cards
        }

    allCards
      .zipWithIndex
      .map {
        case (wrapper, index) => wrapper.copy(id = index + 1)
      }
  }

  private[model] def sort(cards: List[CardWrapper]): List[CardWrapper] =
    cards
      .sortWith {
        case (wrapper1, wrapper2) =>
          val color1 = wrapper1.color
          val color2 = wrapper2.color
          (color1.value == color2.value && wrapper1.card.value < wrapper2.card.value) || color1.value < color2.value
      }

}
