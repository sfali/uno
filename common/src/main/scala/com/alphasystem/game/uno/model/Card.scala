package com.alphasystem.game.uno.model

case class Card(color: Color, card: CardEntry) {
  def isWildCard: Boolean = CardEntry.Wild == card || CardEntry.WildDrawFour == card
}
