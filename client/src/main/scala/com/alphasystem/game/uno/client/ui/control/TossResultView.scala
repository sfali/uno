package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.client.ui.control.delegate.{TossResultView => JTossResultView}
import com.alphasystem.game.uno.model.PlayerDetail
import com.alphasystem.game.uno.model.response.Cards
import javafx.collections.ObservableList
import scalafx.scene.Node

class TossResultView private(override val delegate: JTossResultView) extends Node(delegate) {

  def reset(): Unit = delegate.reset()

  def cardsRow1: ObservableList[Cards] = delegate.getCardsRow1

  def cardsRow2: ObservableList[Cards] = delegate.getCardsRow2

  def cardsRow3: ObservableList[Cards] = delegate.getCardsRow3

  def winners: ObservableList[String] = delegate.getWinners
}

object TossResultView {
  def apply(playerDetail: PlayerDetail): TossResultView = new TossResultView(new JTossResultView(playerDetail))
}
