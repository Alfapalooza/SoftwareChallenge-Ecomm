package org.ecomm.models.responses.exceptions

import org.ecomm.models.{ BundleId, UPC }

case class BundleRequirementNotMetException(upc: UPC, bundleId: BundleId)
  extends JsonServiceResponseException(s"Requirement for item: $upc, for Bundle: $bundleId is not met", 404, 404)
