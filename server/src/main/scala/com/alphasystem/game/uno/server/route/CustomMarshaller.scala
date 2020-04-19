package com.alphasystem.game.uno.server.route

import akka.http.scaladsl.marshalling.Marshaller.fromToEntityMarshaller
import akka.http.scaladsl.marshalling.{ToEntityMarshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK}
import com.alphasystem.game.uno.server.actor.ServerBehavior.{GameAssigned, InvalidAccess}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

trait CustomMarshaller extends FailFastCirceSupport {

  implicit def gameAssignedMarshaller(implicit entity: ToEntityMarshaller[GameAssigned]): ToResponseMarshaller[GameAssigned] =
    fromToEntityMarshaller[GameAssigned](OK)

  implicit def invalidAccessMarshaller(implicit entity: ToEntityMarshaller[InvalidAccess]): ToResponseMarshaller[InvalidAccess] =
    fromToEntityMarshaller[InvalidAccess](BadRequest)

  implicit def internalErrorMarshaller(implicit entity: ToEntityMarshaller[InternalError]): ToResponseMarshaller[InternalError] =
    fromToEntityMarshaller[InternalError](BadRequest)
}
