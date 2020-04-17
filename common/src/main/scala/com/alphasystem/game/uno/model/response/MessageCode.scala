package com.alphasystem.game.uno.model.response

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait MessageCode extends EnumEntry

object MessageCode extends Enum[MessageCode] with CirceEnum[MessageCode] {
  override def values: IndexedSeq[MessageCode] = findValues

  final case object InitiateGame extends MessageCode

  final case object IllegalMove extends MessageCode

  final case object IllegalAccess extends MessageCode

}
