package org.ecomm.controllers

import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import org.ecomm.controllers.directives.MarshallingEntityWithRequestAndModulesAttrDirective
import org.ecomm.controllers.requests.BasketItems
import org.ecomm.guice.ModulesProvider

class Basket(val modulesProvider: ModulesProvider) extends ApiSupport with MarshallingEntityWithRequestAndModulesAttrDirective {
  lazy val routes: Route =
    total

  def total: Route =
    path("total") {
      post {
        requestEntityUnmarshallerWithEntityAndModulesAttr(unmarshaller[BasketItems]) { implicit request =>
          asyncJson {
            modules.basketHelper.calculateTotals(request.body)
          }
        }
      }
    }
}
