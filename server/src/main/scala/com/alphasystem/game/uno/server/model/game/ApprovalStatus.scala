package com.alphasystem.game.uno.server.model.game

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait ApprovalStatus extends EnumEntry

object ApprovalStatus extends Enum[ApprovalStatus] with CirceEnum[ApprovalStatus] {
  override def values: IndexedSeq[ApprovalStatus] = findValues

  final case object Approved extends ApprovalStatus

  final case object Rejected extends ApprovalStatus

  final case object Waiting extends ApprovalStatus

}