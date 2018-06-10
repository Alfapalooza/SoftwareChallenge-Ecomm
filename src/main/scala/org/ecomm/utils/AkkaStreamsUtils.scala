package org.ecomm.utils

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.{ Flow, Source }

import scala.collection.immutable

object AkkaStreamsUtils {
  implicit class Iterable2Source[T](iterable: Iterable[T]) {
    @inline def toSource(implicit materializer: Materializer): Source[T, NotUsed] =
      Source(iterable.to[immutable.Iterable])
  }

  def flatten[T]: Flow[Seq[T], T, NotUsed] =
    Flow[Seq[T]].mapConcat[T](_.to[immutable.Iterable])

  def flattenOpt[T]: Flow[Option[T], T, NotUsed] =
    Flow[Option[T]].mapConcat[T](_.to[immutable.Iterable])
}
