package com.alphasystem.game.uno.model

import com.alphasystem.game.uno.client.model._

case class Card(color: Color, card: CardEntry) {
  def isWildCard: Boolean = CardEntry.Wild == card || CardEntry.WildDrawFour == card

  def toImageCoordinates: (Int, Int) = ImageCoordinates(this)
}


object Card {

  /*
   * Java API
   */
  def create(color: String, card: String): Card = Card(Color.withName(color), CardEntry.withName(card))
}