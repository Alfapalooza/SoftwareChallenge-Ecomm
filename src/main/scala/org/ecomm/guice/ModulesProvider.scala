package org.ecomm.guice

import com.google.inject.Inject

import org.ecomm.configuration.Configuration
import org.ecomm.logger.impl.ApplicationLogger
import org.ecomm.Modules

import akka.http.scaladsl.server.directives.HttpRequestWithEntity

case class ModulesProvider @Inject() (
    configuration: Configuration,
    akka: Akka,
    applicationLogger: ApplicationLogger
) {
  /**
   * Distinction is made between `ModulesProvider` & `Modules`.
   * Modules is initialized and dependent on a request, this is useful
   * for initalizing and containing class members that will have cached assets that are can't be
   * shared between requests. `ModulesProvider` is a subset of `Modules` where
   * the class members can be shared between requests, e.g injected members.
   *
   * @param requestWithEntity - The request to bind this instance of `Modules` to.
   * @tparam T - The request entity type
   * @return
   */
  def apply[T](requestWithEntity: HttpRequestWithEntity[T]): Modules[T] =
    Modules(configuration, akka, applicationLogger)(requestWithEntity)
}

