package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.guice.Akka
import org.ecomm.logger.impl.ApplicationLogger

import akka.http.scaladsl.server.directives.HttpRequestWithEntity

case class Modules[T](
  configuration: Configuration,
  akka: Akka,
  applicationLogger: ApplicationLogger
)(implicit requestWithNoEntity: HttpRequestWithEntity[T])