package com.alphasystem.game.uno.model

import enumeratum.{CirceEnum, EnumEntry, Enum}

sealed trait GameType extends EnumEntry

object GameType extends Enum[GameType] with CirceEnum[GameType] {
  override def values: IndexedSeq[GameType] = findValues

  final case object Classic extends GameType

  final case object Progressive extends GameType

}
