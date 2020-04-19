package com.alphasystem.game.uno.server.model

import com.alphasystem.game.uno.model.response.ResponseEnvelope
import com.alphasystem.game.uno.server.model.game.GameState
import enumeratum.{CirceEnum, Enum, EnumEntry}

/**
 * Represents all the responses returned from actors
 */
sealed trait Event

final case class ResponseEvent(responseEnvelope: ResponseEnvelope) extends Event

final case class ErrorEvent(code: ErrorCode) extends Event

final case class StateInfo(state: GameState) extends Event

case object Finished extends Event

final case class Fail(ex: Exception) extends Event

sealed trait ErrorCode extends EnumEntry

object ErrorCode extends Enum[ErrorCode] with CirceEnum[ErrorCode] {
  override def values: IndexedSeq[ErrorCode] = findValues

  final case object AlreadyPartOfDifferentGame extends ErrorCode

  final case object InvalidStateToJoinGame extends ErrorCode

}


