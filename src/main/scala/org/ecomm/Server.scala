package org.ecomm

import org.ecomm.configuration.Configuration
import org.ecomm.logger.impl.{ ErrorLogger, RequestLogger }

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout

object Server extends App with Routes {
  val configuration: Configuration =
    new Configuration()

  implicit val system: ActorSystem =
    ActorSystem(configuration.name, configuration.underlyingConfig)

  implicit val materializer: ActorMaterializer =
    ActorMaterializer()(system)

  implicit val timeout: Timeout =
    configuration.timeout

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