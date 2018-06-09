package org.ecomm.logger.impl

import com.google.inject.Inject

import org.ecomm.logger.{ Logger, LoggingInformation }

import akka.actor.ActorSystem
import akka.event.{ Logging, LoggingAdapter }

class ErrorLogger @Inject() (actorSystem: ActorSystem) extends Logger {
  override protected val logger: LoggingAdapter =
    Logging(actorSystem, getClass)

  def error[T](throwable: Throwable, elem: T)(implicit loggingInformation: LoggingInformation[(Exception, T)]): Unit =
    error(new Exception(throwable), elem)

  def error[T](exception: Exception, elem: T)(implicit loggingInformation: LoggingInformation[(Exception, T)]): Unit =
    logger.error(loggingInformation.log(exception.getMessage, exception -> elem).toString)
}
