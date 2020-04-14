package com.alphasystem.game.uno.model

case class Card(id: Int, color: Color, card: CardEntry) {
  def isWildCard: Boolean = CardEntry.Wild == card || CardEntry.WildDrawFour == card
}
