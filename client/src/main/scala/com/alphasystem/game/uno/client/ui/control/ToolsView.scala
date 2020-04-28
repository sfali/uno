package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.client.ui.control.delegate.{ToolsView => JToolsView}
import scalafx.Includes._
import scalafx.beans.property._
import scalafx.scene.Node

class ToolsView private(override val delegate: JToolsView)
  extends Node(delegate) {

  def enableStartGameButtonProperty: BooleanProperty = delegate.enableStartGameButtonProperty()

  def enableStartGameButton: Boolean = delegate.getEnableStartGameButton

  def enableStartGameButton_=(enable: Boolean): Unit = delegate.setEnableStartGameButton(enable)
}

object ToolsView {
  def apply(): ToolsView = new ToolsView(new JToolsView())
}
