package akka.http.scaladsl.server.directives

import akka.http.scaladsl.model._

class HttpRequestWithNoEntity(override val request: HttpRequest) extends HttpRequestWithEntity(None, request)