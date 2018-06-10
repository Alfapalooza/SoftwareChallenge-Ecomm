package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.controllers.ApiSupport
import org.ecomm.controllers.requests.BasketItems
import org.ecomm.controllers.directives.RequestResponseHandlingDirective
import org.ecomm.helpers.basket.BasketHelper

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MarshallingEntityWithRequestDirective
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.stream.ActorMaterializer
import akka.util.Timeout

trait Routes extends ApiSupport with RequestResponseHandlingDirective with MarshallingEntityWithRequestDirective {
  def configuration: Configuration

  implicit def actorSystem: ActorSystem

  implicit def actorMaterializer: ActorMaterializer

  implicit def timeout: Timeout

  def basketHelper: BasketHelper

  val routes: Route =
    requestResponseHandler {
      pathPrefix("basket") {
        path("total") {
          post {
            requestEntityUnmarshallerWithEntity(unmarshaller[BasketItems]) { implicit request =>
              asyncJson {
                basketHelper.calculateBasketTotal(request.body)
              }
            }
          }
        }
      }
    }
}
