package ib.data

import ib.util.DateUtil

/**
  * Created by qili on 2015/9/10.
  */

//the other Tweet data will also derive from this parent type
case class TimeSeries(
                       val date: java.util.Date,
                       val price: Double)

case class Quote(date: java.util.Date, open: Double, close: Double, high: Double, low: Double, volume: Double) {
  override def toString: String = DateUtil.SDF.format(date) + "," + open + "," + close + "," + high + "," + low + "," + volume
}

object Quotes {
  def parse(line: String): Quote = {
    val tags = line.split(",")
    val date = DateUtil.SDF.parse(tags.apply(0))
    val open = tags.apply(1).toDouble
    val close = tags.apply(2).toDouble
    val high = tags.apply(3).toDouble
    val low = tags.apply(4).toDouble
    val volume = tags.apply(5).toDouble
    Quote(date, open, close, high, low, volume)
  }
}
