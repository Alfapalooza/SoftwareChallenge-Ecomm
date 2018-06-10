package org.ecomm.helpers.basket

import org.ecomm.controllers.requests.{ BasketItem, BasketItems }
import org.ecomm.models.basket.{ BasketTotal, Items }
import org.ecomm.models.responses.exceptions.ItemNotFoundException
import org.ecomm.utils.AkkaStreamsUtils._
import org.ecomm.utils.PriceUtils._

import akka.http.scaladsl.server.directives.HttpRequestWithEntity
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

import scala.concurrent.{ ExecutionContext, Future }

class BasketHelper[T]()(implicit requestWithEntity: HttpRequestWithEntity[T], materializer: ActorMaterializer) {
  private implicit val ec: ExecutionContext =
    materializer.executionContext

  private val totalFlow =
    Flow[BasketItem].map(item => Items.prices.getOrElse(item.upc, throw ItemNotFoundException(item.upc)))

  def calculateBasketTotal(basketItems: BasketItems): Future[BasketTotal] =
    for {
      total <- calculateTotal(basketItems)
      discount <- calculateDiscount(basketItems)
    } yield BasketTotal(total, discount)

  private def calculateTotal(basketItems: BasketItems): Future[BigDecimal] =
    basketItems
      .items
      .toSource
      .via(totalFlow)
      .runFold(BigDecimal(0))(_ + _)
      .map(_.toPrice)

  private def calculateDiscount(basketItems: BasketItems): Future[BigDecimal] =
    Future.successful(BigDecimal(0))
}
