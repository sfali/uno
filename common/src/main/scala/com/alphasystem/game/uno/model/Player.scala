package com.alphasystem.game.uno.model

case class Player(id: Int, name: String, points: Int = 0, owner: Boolean = false)
