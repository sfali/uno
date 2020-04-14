package com.alphasystem.game.uno.model

case class CardWrapper(id: Int, color: Color, card: CardEntry) {
  def isWildCard: Boolean = CardEntry.Wild == card || CardEntry.WildDrawFour == card
}
