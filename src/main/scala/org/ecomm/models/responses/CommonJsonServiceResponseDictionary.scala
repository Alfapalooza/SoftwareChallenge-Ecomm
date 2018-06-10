package org.ecomm.models.responses

import org.ecomm.models.responses.exceptions.JsonServiceResponseException

object CommonJsonServiceResponseDictionary {
  object E0200 extends DefaultJsonServiceResponse {
    override def msg: String =
      "Ok"

    override def status: Int =
      200

    override def code: Int =
      200
  }

  object E0400 extends JsonServiceResponseException("Bad Request", 400, 400)
  object E0500 extends JsonServiceResponseException("Internal Server Error", 500, 500)
}
