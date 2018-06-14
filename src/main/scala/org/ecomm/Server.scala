package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.logger.impl.{ ErrorLogger, RequestLogger }
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.ecomm.models.{ Price, UPC }
import org.ecomm.models.basket.{ Catalog, Items }
import org.ecomm.models.basket.bundles.BundleDiscount

object Server extends App with Routes {
  val configuration: Configuration =
    new Configuration()

  implicit val system: ActorSystem =
    ActorSystem(configuration.name, configuration.underlyingConfig)

  implicit val materializer: ActorMaterializer =
    ActorMaterializer()(system)

  implicit val timeout: Timeout =
    configuration.timeout

  implicit val catalog: Catalog =
    new Catalog {
      override def priceMap: Map[UPC, Price] =
        Items.priceMap

      override def bundleDiscountsMap: Map[UPC, Seq[BundleDiscount]] =
        BundleDiscount.bundleDiscountsMap
    }

  lazy val requestLogger: RequestLogger =
    new RequestLogger()

  lazy val errorLogger: ErrorLogger =
    new ErrorLogger()

  Http()
    .bindAndHandle(
      routes,
      configuration.interface,
      configuration.port
    )
}