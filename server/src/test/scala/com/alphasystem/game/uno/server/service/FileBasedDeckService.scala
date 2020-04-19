package com.alphasystem.game.uno.server.service

import java.io.File
import java.nio.file.Files

import com.alphasystem.game.uno.model.{Card, Deck}
import io.circe.generic.auto._
import io.circe.parser._

class FileBasedDeckService extends DeckService {

  private var _fileName: String = _

  def fileName: String = _fileName

  def fileName_=(fileName: String): Unit = _fileName = fileName

  override def create(): Deck = {
    val path = new File(getClass.getClassLoader.getResource(fileName).toURI).toPath
    decode[List[Card]](new String( Files.readAllBytes(path))) match {
      case Left(error) => throw error
      case Right(cards) => Deck(cards)
    }
  }
}

object FileBasedDeckService {
  def apply(): FileBasedDeckService = new FileBasedDeckService()
}
