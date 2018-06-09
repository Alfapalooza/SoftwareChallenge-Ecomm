package akka.http.scaladsl.server.directives

import akka.http.scaladsl.model.HttpRequest

class HttpRequestWithEntity[T](val body: T, val request: HttpRequest)
