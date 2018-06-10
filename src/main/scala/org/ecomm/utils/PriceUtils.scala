package org.ecomm.utils

import java.math.{ MathContext, RoundingMode }

import org.ecomm.models.Price

object PriceUtils {
  private val mc: MathContext =
    new MathContext(2, RoundingMode.CEILING)

  implicit class BigDecimal2Price[T](bigDecimal: BigDecimal) {
    @inline def toPrice: Price = bigDecimal.round(mc)
  }
}
