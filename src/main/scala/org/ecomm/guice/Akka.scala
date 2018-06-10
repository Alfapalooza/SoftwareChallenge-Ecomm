package org.ecomm.guice

import com.google.inject.Inject

import org.ecomm.configuration.Configuration

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

class Akka @Inject() (configuration: Configuration) {
  lazy val actorSystem: ActorSystem =
    ActorSystem(configuration.name, configuration.underlyingConfig)

  lazy val actorMaterializer: ActorMaterializer =
    ActorMaterializer()(actorSystem)
}
