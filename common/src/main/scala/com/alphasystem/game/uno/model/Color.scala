package com.alphasystem.game.uno.model

import enumeratum.{CirceEnum, EnumEntry, Enum}

sealed abstract class Color(val value: Int, val hexValue: String) extends EnumEntry {

  /**
   * Contains all the cards except "WildDrawFour"
   */
  val firstSuite: IndexedSeq[CardEntry] = CardEntry.values.dropRight(2)

  /**
   * Contains all the cards except "Zero" and  "Wild"
   */
  val secondSuite: IndexedSeq[CardEntry] = CardEntry.values.dropRight(2).drop(1)
}

object Color extends Enum[Color] with CirceEnum[Color] {
  override def values: IndexedSeq[Color] = findValues

  final case object Red extends Color(1, "#FF0000")

  final case object Green extends Color(2, "#00FF00")

  final case object Blue extends Color(3, "#0000FF")

  final case object Yellow extends Color(4, "#FFFF00")

  final case object Symbol extends Color(value = 5, hexValue = "#000000") {
    override val firstSuite: IndexedSeq[CardEntry] = (1 to 4).map(_ => CardEntry.Wild)

    override val secondSuite: IndexedSeq[CardEntry] = (1 to 4).map(_ => CardEntry.WildDrawFour)
  }

}
