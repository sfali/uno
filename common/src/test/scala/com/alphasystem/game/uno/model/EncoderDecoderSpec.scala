package com.alphasystem.game.uno.model

import com.alphasystem.game.uno.model.request._
import com.alphasystem.game.uno.model.response.{Cards, PlayerInfo, ResponseEnvelope, ResponseType}
import io.circe.parser._
import io.circe.syntax._
import org.scalatest.Assertion
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class EncoderDecoderSpec
  extends AnyFunSpec
    with Matchers {

  describe("Request Encoding/Decoding") {
    it("should encode and decode JoinGame payload") {
      val requestEnvelope = RequestEnvelope(RequestType.StartGame, JoinGame("Player1"))
      val json = """{"type":"start-game","payload":{"name":"Player1"}}"""
      validateRequestEnvelopeEncoding(requestEnvelope, json)
      validateRequestEnvelopeDecoding(requestEnvelope, json)
    }

    it("should encode and decode GameMode payload") {
      val requestEnvelope = RequestEnvelope(RequestType.StartGame, GameMode(GameType.Classic))
      val json = """{"type":"start-game","payload":{"type":"Classic"}}"""
      validateRequestEnvelopeEncoding(requestEnvelope, json)
    }

    it("should encode and Empty payload") {
      val requestEnvelope = RequestEnvelope(RequestType.StartGame, Empty())
      val json = """{"type":"start-game","payload":{}}"""
      validateRequestEnvelopeEncoding(requestEnvelope, json)
      validateRequestEnvelopeDecoding(requestEnvelope, json)
    }
  }

  describe("Response Encoding/Decoding") {
    it("should encode and decode Empty payload") {
      val responseEnvelope = ResponseEnvelope(ResponseType.InitiatingToss, response.Empty())
      val json = """{"type":"initiating-toss","payload":{}}"""
      validateResponseEnvelopeEncoding(responseEnvelope, json)
      validateResponseEnvelopeDecoding(responseEnvelope, json)
    }

    it("should encode and decode PlayerInfo (GameJoined) payload") {
      val responseEnvelope = ResponseEnvelope(ResponseType.GameJoined, PlayerInfo(Player("player1")))
      val json = """{"type":"game-joined","payload":{"player":{"name":"player1"},"otherPlayers":[]}}"""
      validateResponseEnvelopeEncoding(responseEnvelope, json)
      validateResponseEnvelopeDecoding(responseEnvelope, json)
    }

    it("should encode and decode PlayerInfo (NewPlayerJoined) payload") {
      val responseEnvelope = ResponseEnvelope(ResponseType.NewPlayerJoined, PlayerInfo(Player("player1")))
      val json = """{"type":"new-player-joined","payload":{"player":{"name":"player1"},"otherPlayers":[]}}"""
      validateResponseEnvelopeEncoding(responseEnvelope, json)
      validateResponseEnvelopeDecoding(responseEnvelope, json)
    }

    it("should encode and decode GameMode payload") {
      val responseEnvelope = ResponseEnvelope(ResponseType.StartGameRequested, response.StartGameRequest("player1", GameType.Classic))
      val json = """{"type":"start-game-requested","payload":{"type":"Classic"}}"""
      validateResponseEnvelopeEncoding(responseEnvelope, json)
      validateResponseEnvelopeDecoding(responseEnvelope, json)
    }

    it("should encode and decode ChatMessage payload") {
      val responseEnvelope = ResponseEnvelope(ResponseType.ChatMessage, response.ChatMessage("player1", "hello"))
      val json = """{"type":"chat-message","payload":{"playerName":"player1","message":"hello"}}"""
      validateResponseEnvelopeEncoding(responseEnvelope, json)
      validateResponseEnvelopeDecoding(responseEnvelope, json)
    }

    it("should encode and decode Cards payload") {
      val cards = Cards(cards = Card(Color.Yellow, CardEntry.Six) :: Nil)
      val responseEnvelope = ResponseEnvelope(ResponseType.CardPlayed, cards)
      val json = """{"type":"card-played","payload":{"cards":[{"color":"Yellow","card":"Six"}]}}"""
      validateResponseEnvelopeEncoding(responseEnvelope, json)
      validateResponseEnvelopeDecoding(responseEnvelope, json)
    }

    it("should encode and decode TossResult payload") {
      val cards = Cards(Some("Player1"), Card(Color.Yellow, CardEntry.Six) :: Nil) ::
        Cards(Some("Player2"), Card(Color.Red, CardEntry.Nine) :: Nil) :: Nil
      val responseEnvelope = ResponseEnvelope(ResponseType.TossResult, response.TossResult(cards))
      val json =
        """{
          |"type":"toss-result",
          |"payload":{
          |"cards":[
          |{"playerName":"Player1","cards":[{"color":"Yellow","card":"Six"}]},
          |{"playerName":"Player2","cards":[{"color":"Red","card":"Nine"}]}
          |]
          |}
          |}""".stripMargin.replaceAll(System.lineSeparator(), "")
      println(json)
      validateResponseEnvelopeEncoding(responseEnvelope, json)
      validateResponseEnvelopeDecoding(responseEnvelope, json)
    }
  }

  private def validateRequestEnvelopeEncoding(envelope: RequestEnvelope, expected: String): Assertion =
    envelope.asJson.deepDropNullValues.noSpaces should equal(expected)

  private def validateRequestEnvelopeDecoding(expected: RequestEnvelope, json: String): Assertion = {
    decode[RequestEnvelope](json) match {
      case Left(error) => fail(s"${error.getMessage}")
      case Right(actual) => actual shouldBe expected
    }
  }

  private def validateResponseEnvelopeEncoding(envelope: ResponseEnvelope, expected: String): Assertion =
    envelope.asJson.deepDropNullValues.noSpaces should equal(expected)

  private def validateResponseEnvelopeDecoding(expected: ResponseEnvelope, json: String): Assertion = {
    decode[ResponseEnvelope](json) match {
      case Left(error) => fail(s"${error.getMessage}")
      case Right(actual) => actual shouldBe expected
    }
  }
}
