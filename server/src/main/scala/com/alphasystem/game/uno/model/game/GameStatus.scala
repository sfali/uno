package com.alphasystem.game.uno.model.game

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait GameStatus extends EnumEntry

object GameStatus extends Enum[GameStatus] with CirceEnum[GameStatus] {
  override def values: IndexedSeq[GameStatus] = findValues

  final case object Initiated extends GameStatus

  final case object Started extends GameStatus

  final case object Dealing extends GameStatus

  final case object Finished extends GameStatus
}
