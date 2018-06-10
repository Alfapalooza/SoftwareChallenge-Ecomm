package org.ecomm.models.basket.multisave

sealed trait MultisaveOperator

object MultisaveOperator {
  object OR extends MultisaveOperator
  object AND extends MultisaveOperator
}
