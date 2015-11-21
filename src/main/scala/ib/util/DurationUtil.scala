package ib.util

import scala.concurrent.duration.Duration

/**
  * Created by qili on 2015/9/10.
  */
object DurationUtil {

  def duration2String(d: Duration): String = {
    d.toSeconds.toString
  }
}
