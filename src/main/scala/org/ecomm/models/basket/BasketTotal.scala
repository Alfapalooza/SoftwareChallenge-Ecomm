package org.ecomm.models.basket

import org.ecomm.models.Price
import org.ecomm.models.responses.JsonServiceResponse
import org.ecomm.utils.PriceUtils._

import play.api.libs.json.{ Json, Writes }

case class BasketTotal(private val total: BigDecimal, private val discount: BigDecimal) extends JsonServiceResponse[BasketTotal] {
  val finalTotal: Price =
    total.toPrice

  val finalDiscount: Price =
    -discount.toPrice

  lazy val grandTotal: Price =
    (total - discount).toPrice

  override implicit def writes: Writes[BasketTotal] =
    (o: BasketTotal) =>
      Json.obj(
        "total" -> finalTotal,
        "discount" -> finalDiscount,
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
