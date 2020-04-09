package com.alphasystem.game.uno.model

import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

sealed abstract class Color(override val value: Int, hexValue: String) extends IntEnumEntry {

  /**
   * Contains all the cards except "WildDrawFour"
   */
  val firstSuite: IndexedSeq[Card] = Card.values.dropRight(1)

  /**
   * Contains all the cards except "Zero" and  "Wild"
   */
  val secondSuite: IndexedSeq[Card] = Card.values.dropRight(2).drop(1) :+ Card.WildDrawFour
}

object Color extends IntEnum[Color] with IntCirceEnum[Color] {
  override def values: IndexedSeq[Color] = findValues

  final case object Red extends Color(0, "#FF0000")

  final case object Green extends Color(30, "#00FF00")

  final case object Blue extends Color(60, "#0000FF")

  final case object Yellow extends Color(90, "#FFFF00")

}
