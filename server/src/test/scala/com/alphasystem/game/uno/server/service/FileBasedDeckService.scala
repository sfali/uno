package com.alphasystem.game.uno.server.service

import java.io.File
import java.nio.file.Files

import com.alphasystem.game.uno.model.{Card, Deck}
import io.circe.generic.auto._
import io.circe.parser._

class FileBasedDeckService extends DeckService {

  private val path = new File(getClass.getClassLoader.getResource(FileBasedDeckService.FileName).toURI).toPath

  override def create(): Deck = {
    decode[List[Card]](new String(Files.readAllBytes(path))) match {
      case Left(error) => throw error
      case Right(cards) => Deck(cards)
    }
  }
}

object FileBasedDeckService {
  def apply(): FileBasedDeckService = new FileBasedDeckService()

  private val FileName = "deck.json"
}
