package org.ecomm.helpers.basket

import org.ecomm.controllers.requests.{BasketItem, BasketItems}
import org.ecomm.models.{Price, UPC}
import org.ecomm.models.basket.{BasketTotal, Items}
import org.ecomm.models.basket.multisave.Multisave
import org.ecomm.models.responses.exceptions.ItemNotFoundException
import org.ecomm.utils.PriceUtils._

import akka.NotUsed
import akka.http.scaladsl.server.directives.HttpRequestWithEntity
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

import scala.concurrent.Future

object BasketHelper {
  private case class IntermediateItem(upc: UPC, price: Price, multisave: Seq[Multisave])

  //  private val itemQuantityMap: scala.collection.mutable.Map[UPC, Long] =
  //    req
  //      .body
  //      .items
  //      .groupBy(_.upc)
  //      .mapValues {
  //        _.foldLeft(0L) {
  //          case (acc, item) =>
  //            acc + item.quantity
  //        }
  //      }

  private val intermediateItemFlow: Flow[BasketItem, IntermediateItem, NotUsed] =
    Flow[BasketItem]
      .map { item =>
        val itemPrice =
          Items
            .priceMap
            .getOrElse(item.upc, throw ItemNotFoundException(item.upc))
            .toPrice

        val itemMultisave =
          Multisave
            .multisaveMap
            .getOrElse(item.upc, Nil)

        IntermediateItem(
          item.upc,
          itemPrice,
          itemMultisave
        )
      }

  def calculateTotal()(implicit req: HttpRequestWithEntity[BasketItems], materializer: ActorMaterializer): Future[BasketTotal] =
    ???
}
