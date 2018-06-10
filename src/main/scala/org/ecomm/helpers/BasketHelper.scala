package org.ecomm.helpers

import akka.http.scaladsl.server.directives.HttpRequestWithEntity
import org.ecomm.controllers.requests.BasketItems
import org.ecomm.models.helpers.BasketTotal

import scala.concurrent.Future

class BasketHelper[T]()(implicit requestWithEntity: HttpRequestWithEntity[T]) {
  def calculateTotals(basketItems: BasketItems): Future[BasketTotal] =
    Future.successful(new BasketTotal(0, 0))
}
