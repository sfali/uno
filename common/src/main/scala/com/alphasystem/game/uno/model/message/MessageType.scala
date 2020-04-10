package com.alphasystem.game.uno.model.message

import enumeratum.{CirceEnum, Enum, EnumEntry}
import enumeratum.EnumEntry.Hyphencase

sealed trait MessageType extends EnumEntry with Hyphencase

object MessageType extends Enum[MessageType] with CirceEnum[MessageType] {
  override def values: IndexedSeq[MessageType] = findValues

  /**
   * Command to sign in to game server
   */
  final case object SignIn extends MessageType

  /**
   * Command to join a game
   */
  final case object JoinGame extends MessageType

  /**
   * Command to start a game
   */
  final case object StartGame extends MessageType
}
