package org.ecomm.models.basket.bundles

import org.ecomm.models.{ BundleId, Price, UPC }

case class BundleDiscount(id: BundleId, requirements: Seq[BundleDiscountItemRequirements], discountAmount: Price)

object BundleDiscount {
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
          BundleDiscountItemRequirements("1234567890", 3)
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

  lazy val bundleDiscountsMap: Map[UPC, Seq[BundleDiscount]] = {
    var id =
      0L

    bundleDiscountList
      .flatMap { multisave =>
        multisave.requirements.map { requirement =>
          val tuple =
            requirement.upc -> multisave.copy(id = id)

          id = id + 1

          tuple
        }
      }
      .groupBy(_._1)
      .mapValues(_.map(_._2))
  }

  def apply(requirements: Seq[BundleDiscountItemRequirements], discountAmount: Price): BundleDiscount =
    BundleDiscount(0, requirements, discountAmount)
}
