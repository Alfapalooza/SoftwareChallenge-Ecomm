package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.helpers.basket.BasketHelper
import org.ecomm.logger.impl.{ ErrorLogger, RequestLogger }

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout

object Server extends App with Routes {
  val configuration: Configuration =
    new Configuration()

  implicit val actorSystem: ActorSystem =
    ActorSystem(configuration.name, configuration.underlyingConfig)

  implicit val actorMaterializer: ActorMaterializer =
    ActorMaterializer()(actorSystem)

  implicit val timeout: Timeout =
    configuration.timeout

  lazy val requestLogger: RequestLogger =
    new RequestLogger()

  lazy val errorLogger: ErrorLogger =
    new ErrorLogger()

  lazy val basketHelper: BasketHelper =
    new BasketHelper()

  Http()
    .bindAndHandle(
      routes,
      configuration.interface,
      configuration.port
    )
}