package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.client.ui.control.delegate.{GameView => JGameView}
import com.alphasystem.game.uno.model.PlayerDetail
import javafx.collections.ObservableList
import scalafx.Includes._
import scalafx.beans.property._
import scalafx.scene.Node

import scala.jdk.CollectionConverters._

class GameView private(override val delegate: JGameView) extends Node(delegate) {

  def playerDetails: ObservableList[PlayerDetail] = delegate.getPlayerDetails

  def playerDetails_=(playerDetails: List[PlayerDetail]): Unit = delegate.setPlayerDetails(playerDetails.asJava)

  def addPlayer(playerDetail: PlayerDetail): Unit = delegate.addPlayer(playerDetail)

  def selectedPosition: IntegerProperty = delegate.selectedPositionProperty()

  def selectedPosition_=(position: Int): Unit = delegate.setSelectedPosition(position)
}

object GameView {
  def apply(): GameView = new GameView(new JGameView())
}
