package ib.cassandra

import java.util.Date

import com.datastax.spark.connector.SomeColumns
import ib.data.Quotes
import ib.util.DateUtil

/**
  * Created by qili on 09/01/2016.
  */
case class Quote(ticker: String, exchange: String, day:String, date: Date, open: Double, close: Double, high: Double,
                 low: Double, volume: Double)

object Quote {
  def apply(ticker: String, ex: String, s: String): Quote = {
    val q = Quotes.parse(s)
    Quote(ticker, ex, DateUtil.DATE.format(q.date), q.date, q.open, q.close, q.high, q.low, q.volume)
  }

  val allColumns = SomeColumns("ticker", "exchange", "day", "date", "open", "close", "high", "low", "volume")
}
