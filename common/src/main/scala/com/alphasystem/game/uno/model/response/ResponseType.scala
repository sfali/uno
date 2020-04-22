package com.alphasystem.game.uno.model.response

import enumeratum.EnumEntry.Hyphencase
import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait ResponseType extends EnumEntry with Hyphencase

object ResponseType extends Enum[ResponseType] with CirceEnum[ResponseType] {
  override def values: IndexedSeq[ResponseType] = findValues

  final case object NewPlayerJoined extends ResponseType

  final case object GameJoined extends ResponseType

  final case object StartGameRequested extends ResponseType

  final case object InitiatingToss extends ResponseType

  final case object TossResult extends ResponseType

  final case object IllegalAccess extends ResponseType

  final case object InformationMessage extends ResponseType

  final case object ConfirmationMessage extends ResponseType

  final case object WarningMessage extends ResponseType

  final case object ErrorMessage extends ResponseType

  final case object ChatMessage extends ResponseType

}
