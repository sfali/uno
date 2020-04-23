package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.client.ui.control.delegate.{PlayersView => JPlayersView}
import com.alphasystem.game.uno.model.PlayerDetail
import javafx.collections.ObservableList
import scalafx.Includes._
import scalafx.beans.property._
import scalafx.scene.Node

import scala.jdk.CollectionConverters._

class PlayersView private(override val delegate: JPlayersView) extends Node(delegate) {

  def playerDetails: ObservableList[PlayerDetail] = delegate.getPlayerDetails

  def playerDetails_=(playerDetails: List[PlayerDetail]): Unit = delegate.setPlayerDetails(playerDetails.asJava)

  def addPlayer(playerDetail: PlayerDetail): Unit = delegate.addPlayer(playerDetail)

  def myPlayer: PlayerDetail = delegate.getMyPlayer

  def myPlayer_=(playerDetail: PlayerDetail): Unit = delegate.setMyPlayer(playerDetail)

  def selectedPosition: IntegerProperty = delegate.selectedPositionProperty()

  def selectedPosition_=(position: Int): Unit = delegate.setSelectedPosition(position)
}

object PlayersView {
  def apply(): PlayersView = new PlayersView(new JPlayersView())
}
