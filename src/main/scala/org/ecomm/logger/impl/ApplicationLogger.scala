package org.ecomm.logger.impl

import com.google.inject.Inject
import org.ecomm.logger.Logger
import akka.event.{ Logging, LoggingAdapter }
import org.ecomm.guice.Akka

class ApplicationLogger @Inject() (akka: Akka) extends Logger {
  override protected def logger: LoggingAdapter =
    Logging(akka.actorSystem, getClass)
}
