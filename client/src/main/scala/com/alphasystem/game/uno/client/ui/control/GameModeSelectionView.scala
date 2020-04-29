package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.client.ui.control.delegate.{GameModeSelectionView => JGameModeSelectionView}
import com.alphasystem.game.uno.model.GameType
import scalafx.Includes._
import scalafx.beans.property._
import scalafx.scene.Node

class GameModeSelectionView private(override val delegate: JGameModeSelectionView)
  extends Node(delegate) {

  def getSelectedMode: GameType = delegate.getSelectedMode

  def selectedMode: ReadOnlyObjectProperty[GameType] = delegate.selectedModeProperty()
}

object GameModeSelectionView {
  def apply(): GameModeSelectionView = new GameModeSelectionView(new JGameModeSelectionView())
}
