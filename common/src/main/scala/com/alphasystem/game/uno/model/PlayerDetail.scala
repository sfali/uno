package com.alphasystem.game.uno.model

case class PlayerDetail(name: String,
                        points: Int = 0,
                        numberOfCardsLeft: Int = -1)
