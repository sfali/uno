package com.alphasystem.game.uno.model.response

import enumeratum.EnumEntry.Hyphencase
import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait ResponseType extends EnumEntry with Hyphencase

object ResponseType extends Enum[ResponseType] with CirceEnum[ResponseType] {
  override def values: IndexedSeq[ResponseType] = findValues

  final case object None extends ResponseType

}
