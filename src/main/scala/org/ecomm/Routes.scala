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
import org.ecomm.models.basket.Catalog

import scala.concurrent.{ ExecutionContextExecutor, Future }

trait Routes extends ApiSupport with RequestResponseHandlingDirective with MarshallingEntityWithRequestDirective {
  def configuration: Configuration

  implicit def system: ActorSystem

  implicit def timeout: Timeout

  implicit def catalog: Catalog

  val routes: Route =
    requestResponseHandler {
      pathPrefix("basket") {
        path("total") {
          post {
            requestUnmarshallerWithEntity(unmarshaller[BasketItems]) { implicit request =>
              implicit val ec: ExecutionContextExecutor =
                ActorMaterializer().executionContext

              asyncJson {
                Future(BasketHelper.calculateTotalAllPermutations(request.body))
              }
            }
          }
        }
      }
    }
}
