package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.client.ui.control.delegate.{PlayingAreaView => JPlayingAreaView}
import scalafx.scene.Node

class PlayingAreaView private(override val delegate: JPlayingAreaView) extends Node(delegate) {

}

object PlayingAreaView {
  def apply(): PlayingAreaView = new PlayingAreaView(new JPlayingAreaView())
}
