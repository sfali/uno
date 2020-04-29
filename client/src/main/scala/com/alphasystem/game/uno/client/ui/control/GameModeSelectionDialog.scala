package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.model.GameType
import scalafx.scene.control.{ButtonType, Dialog}
import scalafx.stage.{Modality, Window}

class GameModeSelectionDialog(owner: Window) extends Dialog[GameType] {

  private val gameModeSelectionView = GameModeSelectionView()

  initDialog()

  private def initDialog(): Unit = {
    initModality(Modality.WindowModal)
    initOwner(owner)
    title = "Choose Game Mode"
    val pane = dialogPane.get()
    pane.setContent(gameModeSelectionView)
    pane.getButtonTypes.addAll(ButtonType.OK, ButtonType.Cancel)
    val okButton = pane.lookupButton(ButtonType.OK)
    okButton.disableProperty().bind(gameModeSelectionView.selectedMode.isNull)
    resultConverter = bt => {
      if (bt.text == "OK") {
        gameModeSelectionView.getSelectedMode
      } else {
        null
      }

    }
  }
}
