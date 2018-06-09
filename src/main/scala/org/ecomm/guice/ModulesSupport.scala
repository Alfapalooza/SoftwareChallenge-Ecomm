package org.ecomm.guice

import org.ecomm.Modules

import akka.http.scaladsl.server.directives.HttpRequestWithEntity

trait ModulesSupport {
  implicit def modulesProvider: ModulesProvider

  implicit def modules[T](implicit requestWithEntity: HttpRequestWithEntity[T]): Modules[T] =
    modulesProvider(requestWithEntity)
}
