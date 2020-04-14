package com.alphasystem.game.uno.model

import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

sealed abstract class Color(override val value: Int, hexValue: String) extends IntEnumEntry {

  /**
   * Contains all the cards except "WildDrawFour"
   */
  val firstSuite: IndexedSeq[Card] = Card.values.dropRight(2)

  /**
   * Contains all the cards except "Zero" and  "Wild"
   */
  val secondSuite: IndexedSeq[Card] = Card.values.dropRight(2).drop(1)
}

object Color extends IntEnum[Color] with IntCirceEnum[Color] {
  override def values: IndexedSeq[Color] = findValues

  final case object Red extends Color(1, "#FF0000")

  final case object Green extends Color(2, "#00FF00")

  final case object Blue extends Color(3, "#0000FF")

  final case object Yellow extends Color(4, "#FFFF00")

  final case object Symbol extends Color(value = 5, hexValue = "#000000") {
    override val firstSuite: IndexedSeq[Card] = (1 to 4).map(_ => Card.Wild)

    override val secondSuite: IndexedSeq[Card] = (1 to 4).map(_ => Card.WildDrawFour)
  }

}
