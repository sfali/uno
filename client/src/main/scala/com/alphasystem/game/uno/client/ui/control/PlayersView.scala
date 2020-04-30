package com.alphasystem.game.uno.client.ui.control

import com.alphasystem.game.uno.client.ui.control.delegate.{PlayersView => JPlayersView}
import com.alphasystem.game.uno.model.PlayerDetail
import javafx.collections.ObservableList
import scalafx.Includes._
import scalafx.beans.property._
import scalafx.scene.Node

import scala.jdk.CollectionConverters._

class PlayersView private(override val delegate: JPlayersView) extends Node(delegate) {

  def numberOfPlayers: Int = playerDetails.size()

  def playerDetails: ObservableList[PlayerDetail] = delegate.getPlayerDetails

  def playerDetails_=(playerDetails: List[PlayerDetail]): Unit = delegate.setPlayerDetails(playerDetails.asJava)

  def addPlayer(playerDetail: PlayerDetail): Unit = delegate.addPlayer(playerDetail)

  def removePlayer(playerDetail: PlayerDetail): Unit = delegate.removePlayer(playerDetail)

  def myPlayer: PlayerDetail = delegate.getMyPlayer

  def myPlayer_=(playerDetail: PlayerDetail): Unit = delegate.setMyPlayer(playerDetail)

  def selectedPlayer: ObjectProperty[PlayerDetail] = delegate.selectedPlayerProperty()

  def selectedPlayer_=(playerDetail: PlayerDetail): Unit = delegate.setSelectedPlayer(playerDetail)
}

object PlayersView {
  def apply(): PlayersView = new PlayersView(new JPlayersView())
}
