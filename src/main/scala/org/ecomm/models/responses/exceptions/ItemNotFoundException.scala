package org.ecomm.models.responses.exceptions

import org.ecomm.models.UPC

case class ItemNotFoundException(upc: UPC) extends JsonServiceResponseException(s"Item Not Found: $upc", 404, 404)
