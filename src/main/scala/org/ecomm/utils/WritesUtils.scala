package org.ecomm.utils

import play.api.libs.json.{ JsString, Writes }

object WritesUtils {
  implicit val string: Writes[String] =
    (o: String) => JsString(o)
}