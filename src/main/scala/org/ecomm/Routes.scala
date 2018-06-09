package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.controllers.directives.RequestResponseHandlingDirective
import org.ecomm.guice.{ Akka, ModulesSupport }

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.MarshallingEntityWithRequestDirective
import akka.util.Timeout

trait Routes extends ModulesSupport with RequestResponseHandlingDirective with MarshallingEntityWithRequestDirective {
  def akka: Akka

  def configuration: Configuration

  implicit lazy val timeout: Timeout =
    configuration.timeout

  implicit def system: ActorSystem =
    akka.actorSystem

  lazy val defaultRoutes: Route =
    ???

  lazy val pathBindings =
    Map(
      "api" -> defaultRoutes
    )

  lazy val routes: Route =
    requestResponseHandler {
      pathBindings.map {
        case (k, v) => path(k)(v)
      } reduce (_ ~ _)
    }
}
