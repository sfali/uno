package com.alphasystem.game.uno.model

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed abstract class CardEntry(val value: Int, val faceValue: Int) extends EnumEntry with Ordered[CardEntry] {
  override def compare(that: CardEntry): Int = faceValue.compareTo(that.faceValue)
}

object CardEntry extends Enum[CardEntry] with CirceEnum[CardEntry] {
  override def values: IndexedSeq[CardEntry] = findValues

  final case object Zero extends CardEntry(0, 0)

  final case object One extends CardEntry(1, 1)

  final case object Two extends CardEntry(2, 2)

  final case object Three extends CardEntry(3, 3)

  final case object Four extends CardEntry(4, 4)

  final case object Five extends CardEntry(5, 5)

  final case object Six extends CardEntry(6, 6)

  final case object Seven extends CardEntry(7, 7)

  final case object Eight extends CardEntry(8, 8)

  final case object Nine extends CardEntry(9, 9)

  final case object Skip extends CardEntry(10, 20)

  final case object Reverse extends CardEntry(11, 20)

  final case object DrawTwo extends CardEntry(12, 20)

  final case object Wild extends CardEntry(13, 50)

  final case object WildDrawFour extends CardEntry(14, 50)

}
