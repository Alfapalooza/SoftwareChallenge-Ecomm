package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.controllers.ApiSupport
import org.ecomm.controllers.requests.BasketItems
import org.ecomm.controllers.directives.RequestResponseHandlingDirective
import org.ecomm.helpers.basket.{BasketHelper, TotalHelper}

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MarshallingEntityWithRequestDirective
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.util.Timeout

trait Routes extends ApiSupport with RequestResponseHandlingDirective with MarshallingEntityWithRequestDirective {
  def configuration: Configuration

  implicit def system: ActorSystem

  implicit def timeout: Timeout

  val routes: Route =
    requestResponseHandler {
      pathPrefix("basket") {
        path("total") {
          post {
            requestEntityUnmarshallerWithEntity(unmarshaller[BasketItems]) { implicit request =>
              asyncJson {
                BasketHelper.calculateTotal
              }
            }
          }
        }
      }
    }
}
