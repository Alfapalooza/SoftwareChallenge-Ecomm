package org.ecomm.models.basket

import org.ecomm.models.{Price, UPC}

object ItemsResource {
  val priceMap: Map[UPC, Price] =
    Map(
      "1234567890" -> 10,
      "0987654321" -> 10.99,
      "2345678901" -> 9,
      "9876532101" -> 9.99,
      "3456789012" -> 8,
      "8765321098" -> 8.99,
      "4567801234" -> 7,
      "7890123456" -> 7.99,
      "5678901234" -> 6,
      "6789012345" -> 6.99,
    )
}
