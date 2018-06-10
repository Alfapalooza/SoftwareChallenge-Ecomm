package org.ecomm.models.basket

import org.ecomm.models.responses.JsonServiceResponse
import org.ecomm.utils.PriceUtils._

import play.api.libs.json.{ Json, Writes }

case class BasketTotal(total: BigDecimal, discount: BigDecimal) extends JsonServiceResponse[BasketTotal] {
  lazy val grandTotal: BigDecimal =
    (total - discount).toPrice

  override implicit def writes: Writes[BasketTotal] =
    (o: BasketTotal) =>
      Json.obj(
        "total" -> total,
        "discount" -> discount,
        "grandTotal" -> grandTotal
      )

  override val msg: String =
    "Basket Total"

  override val status: Int =
    200

  override val code: Int =
    200

  override val response: BasketTotal =
    this
}
