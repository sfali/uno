package com.alphasystem.game.uno.server.service

import com.alphasystem.game.uno.model.Deck

trait DeckService {
  def create(): Deck
}

object DeckService {
  def apply(): DeckService = new DefaultDeckService()
}

class DefaultDeckService extends DeckService {
  override def create(): Deck = {
    val deck = Deck()
    deck.shuffle(100)
    deck
  }
}

