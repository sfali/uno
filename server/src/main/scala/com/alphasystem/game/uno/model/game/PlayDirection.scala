package com.alphasystem.game.uno.model.game

import enumeratum.EnumEntry.Hyphencase
import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait PlayDirection extends EnumEntry with Hyphencase {
  val reverse: PlayDirection
}

object PlayDirection extends Enum[PlayDirection] with CirceEnum[PlayDirection] {
  override def values: IndexedSeq[PlayDirection] = findValues

  final case object Clockwise extends PlayDirection {
    override val reverse: PlayDirection = PlayDirection.CounterClockwise
  }

  final case object CounterClockwise extends PlayDirection {
    override val reverse: PlayDirection = PlayDirection.Clockwise
  }

}
