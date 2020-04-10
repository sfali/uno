package com.alphasystem.game.uno.model.response

import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Hyphencase

sealed trait ResponseType extends EnumEntry with Hyphencase

object ResponseType extends Enum[ResponseType] with CirceEnum[ResponseType] {
  override def values: IndexedSeq[ResponseType] = findValues
}
