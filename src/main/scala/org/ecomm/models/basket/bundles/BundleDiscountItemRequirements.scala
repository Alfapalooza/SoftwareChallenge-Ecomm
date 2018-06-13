package org.ecomm.models.basket.bundles

import org.ecomm.models.UPC

case class BundleDiscountItemRequirements(upc: UPC, quantity: Long, operator: BundleDiscountOperator = BundleDiscountOperator.OR) {
  // In this case, Greater Than, means better than. It's a better requirement if:
  // - It requires fewer items to satisfy the deal
  def >(requirement: BundleDiscountItemRequirements): Boolean =
    quantity < requirement.quantity

  def <(requirement: BundleDiscountItemRequirements): Boolean =
    ! >(requirement)
}