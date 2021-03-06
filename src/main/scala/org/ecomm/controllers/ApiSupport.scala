package org.ecomm.controllers

import akka.http.scaladsl.server.Directives.{ complete, _ }
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport

import org.ecomm.models.responses.JsonServiceResponse

import play.api.libs.json.{ Json, Writes }

import scala.concurrent.Future
import scala.util.{ Failure, Success }

trait ApiSupport extends PlayJsonSupport {
  def asyncJson[T](
    fn: Future[T],
    Err: Throwable => Throwable = identity
  )(implicit writes: Writes[T]): Route =
    async(fn, (res: T) => complete(Json.toJson(res)), Err)

  def asyncWithDefaultResponse[T, TT <: JsonServiceResponse[_]](
    fn: Future[T],
    Ok: TT,
    Err: Throwable => Throwable = identity
  ): Route =
    async(fn, (_: T) => complete(Ok.toJson), Err)

  def async[T](
    fn: Future[T],
    Ok: T => Route,
    Err: Throwable => Throwable = identity
  ): Route =
    onComplete(fn) {
      case Success(res) =>
        Ok(res)
      case Failure(ex) =>
        throw Err(ex)
    }
}
