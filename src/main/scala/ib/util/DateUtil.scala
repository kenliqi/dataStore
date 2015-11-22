package ib.util

import java.text.SimpleDateFormat

/**
  * Created by qili on 2015/9/10.
  */
object DateUtil {
  val SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val DATE = new SimpleDateFormat("yyyy-MM-dd")

  val EdenTime = DATE.parse("2015-11-02")
}
