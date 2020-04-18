package com.alphasystem.game.uno.model.response

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait MessageCode extends EnumEntry

object MessageCode extends Enum[MessageCode] with CirceEnum[MessageCode] {
  override def values: IndexedSeq[MessageCode] = findValues

}
