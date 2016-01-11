package ib.cassandra

import java.util.Date

import com.datastax.spark.connector.SomeColumns
import ib.data.{Quotes}

/**
  * Created by qili on 09/01/2016.
  */
case class Quote(ticker: String, exchange: String, date: Date, open: Double, close: Double, high: Double,
                 low: Double, volume: Double)

object Quote {
  def apply(ticker: String, ex: String, s: String): Quote = {
    val q = Quotes.parse(s)
    Quote(ticker, ex, q.date, q.open, q.close, q.high, q.low, q.volume)
  }

  val allColumns = SomeColumns("ticker", "exchange", "date", "open", "close", "high", "low", "volume")
}
