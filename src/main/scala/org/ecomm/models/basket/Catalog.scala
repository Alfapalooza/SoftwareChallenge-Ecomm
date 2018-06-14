package org.ecomm.models.basket

import org.ecomm.models.basket.bundles.BundleDiscount
import org.ecomm.models.{ Price, UPC }

trait Catalog {
  def priceMap: Map[UPC, Price]

  def bundleDiscountsMap: Map[UPC, Seq[BundleDiscount]]
}
