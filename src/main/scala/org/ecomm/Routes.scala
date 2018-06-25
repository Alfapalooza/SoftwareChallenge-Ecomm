package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.controllers.ApiSupport
import org.ecomm.controllers.requests.BasketItems
import org.ecomm.controllers.directives.RequestResponseHandlingDirective
import org.ecomm.helpers.basket.BasketHelper
import org.ecomm.models.basket.Catalog

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MarshallingEntityWithRequestDirective
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}

trait Routes extends ApiSupport with RequestResponseHandlingDirective with MarshallingEntityWithRequestDirective {
  def configuration: Configuration

  implicit def system: ActorSystem

  implicit def timeout: Timeout

  implicit def ec: ExecutionContext

  implicit def catalog: Catalog

  private val total: Route =
    path("total") {
      post {
        requestUnmarshallerWithEntity(unmarshaller[BasketItems]) { implicit request =>
          asyncJson {
            Future(BasketHelper.calculateTotalAllPermutations(request.body))
          }
        }
      }
    }

  val routes: Route =
    requestResponseHandler {
      pathPrefix("basket") {
        total
      }
    }
}
