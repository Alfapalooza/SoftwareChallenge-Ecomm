package org.ecomm.models.exceptions

import org.ecomm.models.{ CommonJsonServiceResponseDictionary, DefaultJsonServiceResponse }

import scala.util.control.NoStackTrace

case class JsonServiceResponseException(msg: String, code: Int, status: Int) extends Throwable(msg) with DefaultJsonServiceResponse with NoStackTrace {
  override def apply(message: String): JsonServiceResponseException =
    new JsonServiceResponseException(message, code, status)

  def apply(throwable: Throwable): JsonServiceResponseException =
    apply(new Exception(throwable))

  def apply(exception: Exception): JsonServiceResponseException =
    new JsonServiceResponseException(exception.getMessage, code, status)
}

object JsonServiceResponseException {
  def apply(throwable: Throwable): JsonServiceResponseException =
    apply(throwable)

  def apply(ex: Exception): JsonServiceResponseException =
    CommonJsonServiceResponseDictionary.E0500(ex)
}
