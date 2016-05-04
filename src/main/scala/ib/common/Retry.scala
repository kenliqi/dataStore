package ib.common

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
  * Created by qili on 05/01/2016.
  */
trait Retry extends Loggable {
  @tailrec
  final def retry[T](n: Int)(fn: => T): T = {
    Try {
      logger.info(s"Retry for $n time")
      fn
    } match {
      case Success(s) => s
      case Failure(_) if n > 1 => retry(n - 1)(fn)
      case Failure(f) => throw f
    }
  }
}
