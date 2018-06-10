package org.ecomm.models.responses

import play.api.libs.json.{ JsString, Writes }

trait DefaultJsonServiceResponse extends JsonServiceResponse[String] {
  self =>
  def apply(message: String): DefaultJsonServiceResponse =
    new DefaultJsonServiceResponse {
      override def msg: String =
        message

      override def code: Int =
        self.code

      override def status: Int =
        self.status
    }

  override implicit val writes: Writes[String] =
    (o: String) => JsString(o)

  override val response: String =
    msg
}
