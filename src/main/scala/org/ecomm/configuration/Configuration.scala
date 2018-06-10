package org.ecomm.configuration

import com.typesafe.config.{ Config, ConfigFactory }

import akka.util.Timeout

import scala.concurrent.duration._

class Configuration() {
  lazy val underlyingConfig: Config =
    ConfigFactory.load().resolve()

  lazy val server: Config =
    underlyingConfig.getConfig("akka.server")

  lazy val name: String =
    server.getString("name")

  lazy val interface: String =
    server.getString("interface")

  lazy val port: Int =
    server.getInt("port")

  lazy val timeout: Timeout =
    Timeout(server.getInt("timeout") seconds)
}
