package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.client.ui.control.delegate.{CardsView => JCardsView}
import com.alphasystem.game.uno.model.Card
import scalafx.Includes._
import scalafx.scene.Node

import scala.jdk.CollectionConverters._

class CardsView private(override val delegate: JCardsView) extends Node(delegate) {

  def cards: List[Card] = delegate.getCards.toList

  def cards_=(cards: List[Card]): Unit = delegate.setCards(cards.asJava)
}

object CardsView {
  def apply(): CardsView = new CardsView(new JCardsView(CardView().delegate))
}
