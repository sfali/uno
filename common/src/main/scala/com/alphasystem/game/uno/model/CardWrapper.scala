package com.alphasystem.game.uno.model

case class CardWrapper(id: Int, color: Color, card: Card) {
  def isWildCard: Boolean = Card.Wild == card || Card.WildDrawFour == card
}
