package org.ecomm.models.basket

import org.ecomm.models.Price
import org.ecomm.models.responses.JsonServiceResponse
import org.ecomm.utils.PriceUtils._

import play.api.libs.json.{ Json, Writes }

case class BasketTotal(private val total: BigDecimal, private val discount: BigDecimal) extends JsonServiceResponse[BasketTotal] {
  lazy val finalTotal: Price =
    total.toPrice

  lazy val finalDiscount: Price =
    -discount.toPrice

  lazy val grandTotal: Price =
    (total - discount).toPrice

  override val msg: String =
    "Basket Total"

  override val status: Int =
    200

  override val code: Int =
    200

  override val response: BasketTotal =
    this

  override implicit def writes: Writes[BasketTotal] =
    (o: BasketTotal) =>
      Json.obj(
        "total" -> finalTotal,
        "discount" -> finalDiscount,
        "grandTotal" -> grandTotal
      )

  // In this case, Greater Than, means better than. It's a better cart total if:
  // - The grand total is lesser than
  def >(basketTotal: BasketTotal): Boolean =
    basketTotal.grandTotal > grandTotal

  def <(basketTotal: BasketTotal): Boolean =
    ! >(basketTotal)

  def ==(basketTotal: BasketTotal): Boolean =
    basketTotal.grandTotal == grandTotal
}

object BasketTotal {
  val empty: BasketTotal =
    BasketTotal(0, 0)
}
