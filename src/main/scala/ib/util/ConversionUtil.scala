package ib.util

import scala.util.Try

/**
  * Created by qili on 21/11/2015.
  */
object ConversionUtil {
  implicit def tryToOption[U](t: Try[U]): Option[U] =
    if (t.isFailure) None else Some(t.get)
}
