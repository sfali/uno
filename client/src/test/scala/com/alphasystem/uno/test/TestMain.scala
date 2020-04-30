package com.alphasystem.uno.test

import com.alphasystem.game.uno.client.ui.control.{CardView, GameModeSelectionDialog}
import com.alphasystem.game.uno.model.{Card, CardEntry, Color => UnoColor}
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color

object TestMain extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "Test"
    width = 600
    height = 600
    scene = new Scene {
      fill = Color.LightGreen
      private val view: CardView = CardView(Card(UnoColor.Red, CardEntry.Reverse), null, 512)
      view.selected.addListener {
        (_, _, _) =>
          val v = new GameModeSelectionDialog(stage).showAndWait()
          println(v)
      }
      content = view
    }
  }
}
