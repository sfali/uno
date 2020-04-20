package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.client.ui.control.delegate.{CardView => JCardView}
import com.alphasystem.game.uno.model.Card
import javafx.beans.property
import scalafx.Includes._
import scalafx.beans.property._
import scalafx.scene.Node

class CardView private(override val delegate: JCardView) extends Node(delegate) {

  def this(card: Card, playerName: String, height: Int) = this(new JCardView(card, playerName, height))

  def selected: BooleanProperty = delegate.selectedProperty

  def selected_=(selected: Boolean): Unit = delegate.setSelected(selected)

  def card: ObjectProperty[Card] = delegate.cardProperty()

  def card_=(card: Card): Unit = delegate.setCard(card)

  def playerName: property.StringProperty = delegate.playerNameProperty()

  def playerName_=(playerName: String): Unit = delegate.setPlayerName(playerName)

  def fitHeight: Int = delegate.getFitHeight

  def fitHeight_=(height: Int): Unit = delegate.setFitHeight(height)
}

object CardView {
  def apply(): CardView = CardView(null, null)

  def apply(card: Card, playerName: String, height: Int = 128): CardView = new CardView(card, playerName, height)
}
