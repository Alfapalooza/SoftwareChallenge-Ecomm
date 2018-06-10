package org.ecomm.controllers.directives

import org.ecomm.Modules
import org.ecomm.guice.ModulesProvider

import akka.http.libs.typedmap.{ TypedEntry, TypedMap }
import akka.http.scaladsl.server.directives.BasicDirectives.provide
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.{ HttpRequestWithEntity, MarshallingEntityWithRequestDirective }
import akka.http.scaladsl.unmarshalling.{ FromEntityUnmarshaller, FromRequestUnmarshaller }

trait MarshallingEntityWithRequestAndModulesAttrDirective extends MarshallingEntityWithRequestDirective {
  def modulesProvider: ModulesProvider

  def modules[T](implicit requestWithEntity: HttpRequestWithEntity[T]): Modules[T] =
    modulesProvider(requestWithEntity)

  def requestEntityUnmarshallerWithEntityAndModulesAttr[T](um: FromEntityUnmarshaller[T]): Directive1[HttpRequestWithEntity[T]] =
    requestEntityUnmarshallerWithEntity(um).tflatMap(appendAttrs)

  def requestUnmarshallerWithEntityAndAttr[T](um: FromRequestUnmarshaller[T]): Directive1[HttpRequestWithEntity[T]] =
    requestUnmarshallerWithEntity(um).tflatMap(appendAttrs)

  private def appendAttrs[T](requestWithEntity: Tuple1[HttpRequestWithEntity[T]]): Directive1[HttpRequestWithEntity[T]] = {
    val req =
      requestWithEntity._1

    val modules =
      Modules(modulesProvider)(req)

    val attrs =
      TypedMap(TypedEntry(modulesProvider.ModulesAttr[T], modules))

    provide(req.withAttrs(attrs))
  }
}
