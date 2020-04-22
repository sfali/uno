package com.alphasystem.game.uno.model

case class PlayerDetail(position: Int,
                        name: String,
                        points: Int = 0,
                        numberOfCardsLeft: Int = -1)
