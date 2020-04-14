package com.alphasystem.game.uno.model.response

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait MessageType extends EnumEntry with EnumEntry.Lowercase

object MessageType extends Enum[MessageType] with CirceEnum[MessageType] {
  override def values: IndexedSeq[MessageType] = findValues

  final case object Information extends MessageType

  final case object Confirmation extends MessageType

  final case object Warning extends MessageType

  final case object Error extends MessageType

}
