package org.ecomm.models.basket.bundles

sealed trait BundleDiscountOperator

object BundleDiscountOperator {
  object OR extends BundleDiscountOperator
  object AND extends BundleDiscountOperator
}
