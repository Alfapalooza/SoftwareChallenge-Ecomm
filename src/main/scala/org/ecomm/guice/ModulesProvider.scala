package org.ecomm.guice

import com.google.inject.Inject

import org.ecomm.configuration.Configuration
import org.ecomm.logger.impl.ApplicationLogger
import org.ecomm.Modules

import akka.http.libs.typedmap.TypedKey
import akka.http.scaladsl.server.directives.HttpRequestWithEntity

class ModulesProvider @Inject() (
    val configuration: Configuration,
    val akka: Akka,
    val applicationLogger: ApplicationLogger
) {
  val ModulesAttr: TypedKey[Modules[_]] =
    TypedKey[Modules[_]]("Modules")

  /**
   * Distinction is made between [[ModulesProvider]] & [[Modules]].
   * [[Modules]] is initialized and dependent on a request, this is useful
   * for initalizing and containing class members that will have cached assets that can't be
   * shared between requests. [[ModulesProvider]] is a subset of [[Modules]] where
   * the class members can be shared between requests, e.g injected members.
   *
   * @param req - The request to bind this instance of [[Modules]] to.
   * @tparam T - The request entity type
   * @return ModulesProvider with request applied to initialize class members dependent on the request
   */
  def apply[T](req: HttpRequestWithEntity[T]): Modules[_] =
    req.getAttr(ModulesAttr).getOrElse(throw new Exception(s"Binding error `Modules` to request"))
}

