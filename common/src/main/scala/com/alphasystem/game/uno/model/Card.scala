package com.alphasystem.game.uno.model

import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

sealed abstract class Card(override val value: Int, val faceValue: Int) extends IntEnumEntry

object Card extends IntEnum[Card] with IntCirceEnum[Card] {
  override def values: IndexedSeq[Card] = findValues

  final case object Zero extends Card(0, 0)

  final case object One extends Card(1, 1)

  final case object Two extends Card(2, 2)

  final case object Three extends Card(3, 3)

  final case object Four extends Card(4, 4)

  final case object Five extends Card(5, 5)

  final case object Six extends Card(6, 6)

  final case object Seven extends Card(7, 7)

  final case object Eight extends Card(8, 8)

  final case object Nine extends Card(9, 9)

  final case object Skip extends Card(10, 20)

  final case object Reverse extends Card(11, 20)

  final case object DrawTwo extends Card(12, 20)

  final case object Wild extends Card(13, 50)

  final case object WildDrawFour extends Card(14, 50)

}
