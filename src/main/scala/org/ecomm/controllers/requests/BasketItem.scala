package org.ecomm.controllers.requests

import org.ecomm.models.UPC
import play.api.libs.functional.syntax._
import play.api.libs.json.{ JsPath, Reads }

case class BasketItem(upc: UPC, quantity: Long)

object BasketItem {
  implicit val reads: Reads[BasketItem] =
    ((JsPath \ "upc").read[UPC] and
      (JsPath \ "quantity").read[Long])(BasketItem.apply _)
}