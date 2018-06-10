package org.ecomm.models.responses

import play.api.libs.json.{ JsObject, Json, Writes }

object JsonServiceResponse {
  implicit val writes: Writes[JsonServiceResponse[_]] =
    (o: JsonServiceResponse[_]) => o.toJson

  def apply[T](_status: Int, _code: Int, _response: T, _msg: String = "response")(implicit _writes: Writes[T]): JsonServiceResponse[T] =
    new JsonServiceResponse[T] {
      override implicit val writes: Writes[T] =
        _writes

      override val msg: String =
        _msg

      override val code: Int =
        _code

      override val status: Int =
        _status

      override val response: T =
        _response
    }
}

trait JsonServiceResponse[T] {
  implicit def writes: Writes[T]

  def msg: String

  def status: Int

  def code: Int

  def response: T

  def toJson: JsObject =
    Json.obj(
      "message" -> msg,
      "status" -> status,
      "code" -> code,
      "response" -> Json.toJson(response)
    )
}

