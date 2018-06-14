package org.ecomm.utils

import org.ecomm.models.Price

import scala.math.BigDecimal.RoundingMode

object PriceUtils {
  implicit class BigDecimal2Price[T](bigDecimal: BigDecimal) {
    @inline def toPrice: Price = bigDecimal.setScale(2, RoundingMode.HALF_EVEN)
  }
}
