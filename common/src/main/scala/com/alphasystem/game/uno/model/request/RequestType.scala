package com.alphasystem.game.uno.model.request

import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Hyphencase

sealed trait RequestType extends EnumEntry with Hyphencase

object RequestType extends Enum[RequestType] with CirceEnum[RequestType] {
  override def values: IndexedSeq[RequestType] = findValues

  /**
   * Command to start a game
   */
  final case object StartGame extends RequestType
}
