package ib.cassandra

import java.util.Date

import ib.Env
import ib.data.stock.StockRegistry
import ib.data.{Quotes, Quote, Generic}
import ib.spark.Spark._
import com.datastax.spark.connector._

import scala.io.Source

/**
  * Created by qili on 22/11/2015.
  */

/**
  * PartitionKey ticker + datetime, as the datetime is sequence, cassandra will treat it as timestamp as a column
  * per daily quotes - 360, as cassandra will support 2billions columns, so even for 10 years data, one stock just
  * needs 360*360*10=10millions, so it's enough
  * @param ticker
  * @param date
  * @param open
  * @param close
  * @param high
  * @param low
  * @param volume
  */
case class TickerQuote(ticker: String, date: Date, open: Double, close: Double, high: Double,
                       low: Double, volume: Double)

object TickerQuote {
  def apply(ticker: String, s: String): TickerQuote = {
    val q = Quotes.parse(s)
    TickerQuote(ticker, q.date, q.open, q.close, q.high, q.low, q.volume)
  }

  val allColumns = SomeColumns("ticker", "date", "open", "close", "high", "low", "volume")
}

object QuoteLoading extends Generic {
  def all(implicit env: Env.Value) = sc.cassandraTable[TickerQuote](env, classOf[TickerQuote])

  def save(Quotes: Seq[TickerQuote])(implicit env: Env.Value) = {
    val rdd = sc.parallelize(Quotes)
    rdd.saveToCassandra(env, classOf[TickerQuote])
  }

  def query(sql: String)(implicit env: Env.Value) = sc.sql(sql)

  def main(args: Array[String]) {
    implicit val env = Env.DEV
    val filePath = "/Users/qili/finance/data/"
    val tickers = StockRegistry.all
    for (t <- tickers) {
      val quotes: Seq[TickerQuote] = Source.fromFile(filePath + t.symbol + ".txt").getLines().map(s => TickerQuote(t.symbol, s)).toSeq
      save(quotes)
      println(s"Saved ${quotes.size} quotes to $t")
    }
    println("Done!")
  }

}
