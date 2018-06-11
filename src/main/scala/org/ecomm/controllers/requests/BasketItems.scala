package org.ecomm.controllers.requests

import play.api.libs.json.{ JsPath, Reads }

case class BasketItems(items: IndexedSeq[BasketItem])

object BasketItems {
  implicit val reads: Reads[BasketItems] =
    (JsPath \ "items")
      .read[IndexedSeq[BasketItem]]
      .map(BasketItems.apply)
}
