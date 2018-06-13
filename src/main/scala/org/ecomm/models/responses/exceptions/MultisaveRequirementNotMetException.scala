package org.ecomm.models.responses.exceptions

import org.ecomm.models.{ MultisaveId, UPC }

case class MultisaveRequirementNotMetException(upc: UPC, multisaveId: MultisaveId)
  extends JsonServiceResponseException(s"Requirement for item: $upc, for Multisave: $multisaveId is not met", 404, 404)
