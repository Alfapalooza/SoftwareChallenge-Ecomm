package org.ecomm.models.basket.multisave

import org.ecomm.models.UPC

case class MultiSaveItemRequirements(upc: UPC, quantity: Long, operator: MultisaveOperator = MultisaveOperator.OR) {
  // In this case, Greater Than, means better than. It's a better requirement if:
  // - It requires fewer items to satisfy the deal
  def >(requirement: MultiSaveItemRequirements): Boolean =
    quantity < requirement.quantity

  def <(requirement: MultiSaveItemRequirements): Boolean =
    ! >(requirement)
}