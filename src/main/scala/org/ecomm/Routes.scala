package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.controllers.directives.RequestResponseHandlingDirective
import org.ecomm.guice.{Akka, ModulesSupport}
import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.util.Timeout
import org.ecomm.controllers.Basket
import org.ecomm.models.CommonJsonServiceResponseDictionary.E0200
import play.api.libs.json.Json

trait Routes extends ModulesSupport with RequestResponseHandlingDirective {
  def akka: Akka

  def configuration: Configuration

  implicit lazy val timeout: Timeout =
    configuration.timeout

  implicit def system: ActorSystem =
    akka.actorSystem

  lazy val defaultRoutes: Route =
    get {
      complete(OK -> Json.toJson(E0200("Pong!")))
    }

  lazy val pathBindings =
    Map(
      "ping" -> defaultRoutes,
      "basket" -> new Basket(modulesProvider).routes,
    )

  lazy val routes: Route =
    requestResponseHandler {
      pathBindings.map {
        case (k, v) => path(k)(v)
      } reduce (_ ~ _)
    }
}
