package com.alphasystem.game.uno.client

import com.alphasystem.game.uno.model.{Card, CardEntry, Color}

package object model {

  private def coordinates(color: Color, y: Int) =
    color
      .firstSuite
      .foldLeft((Map.empty[Card, (Int, Int)], 0)) {
        case ((map, z), cardEntry) =>
          (map + (Card(color, cardEntry) -> ((z, y))), z + 240)
      }._1

  val ImageCoordinates: Map[Card, (Int, Int)] =
    coordinates(Color.Red, 0) ++ coordinates(Color.Yellow, 360) ++ coordinates(Color.Green, 720) ++
      coordinates(Color.Blue, 1080) ++ Map(Card(Color.Symbol, CardEntry.Wild) -> ((3120, 360))) ++
      Map(Card(Color.Symbol, CardEntry.WildDrawFour) -> ((3120, 1440)))
}
