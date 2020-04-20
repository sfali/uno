package com.alphasystem.uno.test

import com.alphasystem.game.uno.client.ui.control.CardView
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
      content = CardView(Card(UnoColor.Red, CardEntry.Reverse), "Player1", 512)
    }
  }
}
