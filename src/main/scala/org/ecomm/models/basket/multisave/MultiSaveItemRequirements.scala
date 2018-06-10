package org.ecomm.models.basket.multisave

import org.ecomm.models.UPC

case class MultiSaveItemRequirements(upc: UPC, quantity: Long, operator: MultisaveOperator = MultisaveOperator.OR)