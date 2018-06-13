package org.ecomm.models.basket.bundles

import org.ecomm.models.{ BundleId, Price, UPC }

case class BundleDiscount(id: BundleId, requirements: Seq[BundleDiscountItemRequirements], discountAmount: Price)

object BundleDiscount {
  private val bundleDiscountList =
    Seq.empty[BundleDiscount]

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
