package org.ecomm.controllers.requests

import play.api.libs.json.{ JsPath, Reads }

case class BasketItems(items: Seq[BasketItem])

object BasketItems {
  implicit val reads: Reads[BasketItems] =
    (JsPath \ "items")
      .read[Seq[BasketItem]]
      .map(BasketItems.apply)
}
