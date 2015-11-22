package ib.common

import org.slf4j.LoggerFactory

/**
  * Created by qili on 22/11/2015.
  */
trait Loggable {
  val logger = LoggerFactory.getLogger(this.getClass)
}
