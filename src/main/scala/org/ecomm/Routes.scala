package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.controllers.ApiSupport
import org.ecomm.controllers.requests.BasketItems
import org.ecomm.controllers.directives.{ MarshallingEntityWithRequestAndModulesAttrDirective, RequestResponseHandlingDirective }
import org.ecomm.guice.Akka

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.util.Timeout

trait Routes extends ApiSupport with RequestResponseHandlingDirective with MarshallingEntityWithRequestAndModulesAttrDirective {
  def akka: Akka

  def configuration: Configuration

  implicit lazy val timeout: Timeout =
    configuration.timeout

  implicit lazy val system: ActorSystem =
    akka.actorSystem

  lazy val routes: Route =
    requestResponseHandler {
      pathPrefix("basket") {
        path("total") {
          post {
            requestEntityUnmarshallerWithModulesAttr(unmarshaller[BasketItems]) { implicit request =>
              asyncJson {
                modules.basketHelper.calculateBasketTotal(request.body)
              }
            }
          }
        }
      }
    }
}
