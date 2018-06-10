package org.ecomm.models.helpers

import java.math.{ MathContext, RoundingMode }

import org.ecomm.models.JsonServiceResponse
import play.api.libs.json.{ Json, Writes }

class BasketTotal(private val unroundedTotal: BigDecimal, private val unroundedDiscounts: BigDecimal) extends JsonServiceResponse[BasketTotal] {
  private val mc: MathContext =
    new MathContext(2, RoundingMode.CEILING)

  lazy val total: BigDecimal =
    unroundedTotal.round(mc)

  lazy val discount: BigDecimal =
    unroundedDiscounts.round(mc)

  lazy val grandTotal: BigDecimal =
    0

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
