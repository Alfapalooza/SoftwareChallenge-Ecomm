package org.ecomm.helpers

import org.ecomm.controllers.requests.{ BasketItem, BasketItems }
import org.ecomm.helpers.basket.BasketHelper
import org.ecomm.models.{ Price, UPC }
import org.ecomm.models.basket.{ Catalog, ItemsResource }
import org.ecomm.models.basket.bundles.{ BundleDiscount, BundleDiscountResource }

import org.scalatest._

class BasketHelperSpec extends FlatSpec {
  implicit val catalog: Catalog =
    new Catalog {
      override def priceMap: Map[UPC, Price] =
        ItemsResource.priceMap

      override def bundleDiscountsMap: Map[UPC, Seq[BundleDiscount]] =
        BundleDiscountResource.bundleDiscountsMap
    }

  def createBasket(items: Seq[(UPC, Long)]): BasketItems =
    BasketItems(items.map((BasketItem.apply _).tupled))

  it should "not apply discount when no discounts apply" in {
    val item =
      "3456789012"

    val itemQuantity =
      3

    val itemPrice =
      ItemsResource.priceMap(item) * itemQuantity

    val basket =
      createBasket(Seq(item -> itemQuantity))

    val basketTotal =
      BasketHelper.calculateTotal(basket)

    assert(basketTotal.finalDiscount == 0)
    assert(basketTotal.finalTotal == basketTotal.grandTotal)
    assert(basketTotal.grandTotal == itemPrice)
  }

  it should "apply a basic discount when requirements are met" in {
    val item =
      "1234567890"

    val itemQuantity =
      9

    val itemPrice =
      ItemsResource.priceMap(item) * itemQuantity

    val basket =
      createBasket(Seq(item -> itemQuantity))

    val basketTotal =
      BasketHelper.calculateTotal(basket)

    assert(basketTotal.finalDiscount == -30)
    assert(basketTotal.finalTotal == itemPrice)
    assert(basketTotal.grandTotal == basketTotal.finalTotal - 30)
  }

  it should "apply a more complex discount when multiple requirements are met" in {
    val item1 =
      "1234567890"

    val item1Quantity =
      3

    val item1Price =
      ItemsResource.priceMap(item1) * item1Quantity

    val item2 =
      "2345678901"

    val item2Quantity =
      10

    val item2Price =
      ItemsResource.priceMap(item2) * item2Quantity

    val basket =
      createBasket(Seq(item1 -> item1Quantity, item2 -> item2Quantity))

    val basketTotal =
      BasketHelper.calculateTotal(basket)

    assert(basketTotal.finalDiscount == -10.43)
    assert(basketTotal.finalTotal == item1Price + item2Price)
    assert(basketTotal.grandTotal == basketTotal.finalTotal - 10.43)
  }

  it should "apply an even more complex discount when requirements overlap, favouring the highest discount" in {
    val item1 =
      "1234567890"

    val item1Quantity =
      5

    val item1Price =
      ItemsResource.priceMap(item1) * item1Quantity

    val item2 =
      "2345678901"

    val item2Quantity =
      20

    val item2Price =
      ItemsResource.priceMap(item2) * item2Quantity

    val basket =
      createBasket(Seq(item1 -> item1Quantity, item2 -> item2Quantity))

    val basketTotal =
      BasketHelper.calculateTotal(basket)

    assert(basketTotal.finalDiscount == -20.43)
    assert(basketTotal.finalTotal == item1Price + item2Price)
    assert(basketTotal.grandTotal == basketTotal.finalTotal - 20.43)
  }

  //Combinatronics problem, lowest price should in fact be 20$, not 10$. This is trumped
  //by the fact that we don't try every possible discount combinations. If a bundle discount for 10$ when buying
  //1 of product XYZ, and 1 of some "secondary" product then it's applied, but if we encounter a better deal in the future for the
  //"secondary" product from the deal above then it can't be applied since the product is already spoken for.
  it should "apply a relatively simple discount where the lowest price isn't applied due to system design" in {
    val item1 =
      "8765321098"

    val item1Quantity =
      1

    val item1Price =
      ItemsResource.priceMap(item1) * item1Quantity

    val item2 =
      "7890123456"

    val item2Quantity =
      1

    val item2Price =
      ItemsResource.priceMap(item2) * item2Quantity

    val basket =
      createBasket(Seq(item1 -> item1Quantity, item2 -> item2Quantity))

    val basketTotal =
      BasketHelper.calculateTotal(basket)

    assert(basketTotal.finalDiscount == -10)
    assert(basketTotal.finalTotal == item1Price + item2Price)
    assert(basketTotal.grandTotal == basketTotal.finalTotal - 10)
  }

  it should "hit the jackpot when adding item '6789012345'" in {
    val item =
      "6789012345"

    val itemQuantity =
      1

    val itemPrice =
      ItemsResource.priceMap(item) * itemQuantity

    val basket =
      createBasket(Seq(item -> itemQuantity))

    val basketTotal =
      BasketHelper.calculateTotal(basket)

    assert(basketTotal.finalDiscount == -1000)
    assert(basketTotal.grandTotal == itemPrice - 1000)
  }
}
