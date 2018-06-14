package org.ecomm.models.basket.bundles

import org.ecomm.models.UPC

object BundleDiscountResource {
  private val bundleDiscountList: Seq[BundleDiscount] =
    Seq(
      //Buy 3 get 1 free
      BundleDiscount(
        Seq(
          BundleDiscountItemRequirements("1234567890", 3)
        ),
        10
      ),
      //Buy 10 save slightly
      BundleDiscount(
        Seq(
          BundleDiscountItemRequirements("2345678901", 10)
        ),
        0.42873498234
      ),
      //Slightly more complicated, Buy 10 2345678901 & 3 1234567890, get 20. Should be favored over the previous two.
      BundleDiscount(
        Seq(
          BundleDiscountItemRequirements("2345678901", 10, BundleDiscountOperator.AND),
          BundleDiscountItemRequirements("1234567890", 5)
        ),
        20
      ),
      //Buy 1 of item 7890123456, and 1 of item 8765321098, get 10
      BundleDiscount(
        Seq(
          BundleDiscountItemRequirements("7890123456", 1),
          BundleDiscountItemRequirements("8765321098", 1)
        ),
        10
      ),
      //Buy 1 of item 7890123456, get 20
      BundleDiscount(
        Seq(
          BundleDiscountItemRequirements("7890123456", 1)
        ),
        20
      ),
      //Buy 1 get 1000$
      BundleDiscount(
        Seq(
          BundleDiscountItemRequirements("6789012345", 1)
        ),
        1000
      )
    )

  lazy val bundleDiscountsMap: Map[UPC, Seq[BundleDiscount]] =
    BundleDiscount.convertBundleDiscountListToMap(bundleDiscountList)
}
