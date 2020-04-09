package com.alphasystem.game.uno

package object model {

  val MaxCards: Int = 7

  private[model] def buildDeck(): List[CardWrapper] = {
    val initialList = Color
      .values
      .flatMap {
        color =>
          val value = color.value
          buildSuite(value, color, color.firstSuite) ::: buildSuite(value + Card.values.length, color, color.secondSuite)
      }.toList


    // move wild cards at the end, this will help sorting
    val otherCards = initialList.filterNot(wrapper => wrapper.card == Card.Wild || wrapper.card == Card.WildDrawFour)
    val id = initialList.last.id + 1
    val wildCards = initialList.filter(wrapper => wrapper.card == Card.Wild || wrapper.card == Card.WildDrawFour)
      .zipWithIndex
      .map {
        case (wrapper, i) => wrapper.copy(id = id + i)
      }

    otherCards ::: wildCards
  }

  private[model] def sort(cards: List[CardWrapper]): List[CardWrapper] = {
    // separate wild cards from other cards, wild cards will be moved at the end
    val wildCards = cards.filter(_.isWildCard).sortBy(_.id)
    val otherCards = cards.filterNot(_.isWildCard)
      .sortWith {
        case (wrapper1, wrapper2) =>
          val color1 = wrapper1.color
          val color2 = wrapper2.color
          (color1.value == color2.value && wrapper1.card.value < wrapper2.card.value) || color1.value < color2.value
      }
    otherCards ::: wildCards
  }

  private[model] def buildSuite(value: Int, color: Color, suite: IndexedSeq[Card]) =
    suite.map(card => CardWrapper(value + card.value, color, card)).toList
}
