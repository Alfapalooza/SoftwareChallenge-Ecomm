package org.ecomm.logger.impl

import org.ecomm.logger.{ Logger, LoggingInformation }

import akka.event.{ Logging, LoggingAdapter }

import akka.actor.ActorSystem

class ErrorLogger()(implicit actorSystem: ActorSystem) extends Logger {
  override protected val logger: LoggingAdapter =
    Logging(actorSystem, getClass)

  def error[T](throwable: Throwable, elem: T)(implicit loggingInformation: LoggingInformation[(Throwable, T)]): Unit =
    logger.error(loggingInformation.log(throwable.getMessage, throwable -> elem).toString)
}
