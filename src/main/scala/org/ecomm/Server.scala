package org.ecomm

import com.google.inject.{ Guice, Injector }

import org.ecomm.configuration.Configuration
import org.ecomm.guice.{ Akka, ModulesProvider }
import org.ecomm.guice.modules.ModuleBindings
import org.ecomm.logger.impl.{ ErrorLogger, RequestLogger }

import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Server extends App with Routes {
  import net.codingwell.scalaguice.InjectorExtensions._

  private val injector: Injector =
    Guice.createInjector(new ModuleBindings())

  override lazy val modulesProvider: ModulesProvider =
    injector.instance[ModulesProvider]

  override lazy val akka: Akka =
    modulesProvider.akka

  override lazy val configuration: Configuration =
    modulesProvider.configuration

  override lazy val requestLogger =
    injector.instance[RequestLogger]

  override lazy val errorLogger =
    injector.instance[ErrorLogger]

  implicit val materializer: ActorMaterializer =
    akka.actorMaterializer

  Http()
    .bindAndHandle(
      routes,
      configuration.interface,
      configuration.port
    )
}