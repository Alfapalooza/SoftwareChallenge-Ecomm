package org.ecomm.models.responses.exceptions

import org.ecomm.models.responses.{ CommonJsonServiceResponseDictionary, DefaultJsonServiceResponse }

import scala.util.control.NoStackTrace

class JsonServiceResponseException(val msg: String, val code: Int, val status: Int) extends Throwable(msg) with DefaultJsonServiceResponse with NoStackTrace {
  override def apply(message: String): JsonServiceResponseException =
    new JsonServiceResponseException(message, code, status)

  def apply(throwable: Throwable): JsonServiceResponseException =
    new JsonServiceResponseException(throwable.getMessage, code, status)
}

object JsonServiceResponseException {
  def apply(throwable: Throwable): JsonServiceResponseException =
    CommonJsonServiceResponseDictionary.E0500(throwable)
}
