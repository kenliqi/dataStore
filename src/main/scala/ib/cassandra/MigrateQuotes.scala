package ib.cassandra

import ib.Env
import ib.data.{Exchange, Generic}
import ib.data.stock.StockRegistry
import ib.spark.Spark._
import com.datastax.spark.connector._

/**
  * Created by qili on 09/01/2016.
  */
object MigrateQuotes extends Generic {
  implicit val env = Env.DEV

  def main(args: Array[String]) {
    def oldQuotes = sc.cassandraTable[TickerQuote](env, classOf[TickerQuote])
    def newQuotes = sc.cassandraTable[Quote](env, classOf[Quote])
    val exchanges = Seq(Exchange.SHA, Exchange.AMEX, Exchange.NASDAQ, Exchange.NYSE)
    for (e <- exchanges) {
      for (s <- StockRegistry.exchange(e)) {
        val data = oldQuotes.where("ticker = ?", s.symbol)
        val newData = data.map(d => Quote(ticker = d.ticker, e.name(), date = d.date, open = d.open, high = d.high, low = d.low, close = d.close, volume = d.volume))
        val collectData = sc.parallelize(newData.collect().toSeq)
        collectData.saveToCassandra(env, classOf[Quote], Quote.allColumns)
        println(s"Saved ${newData.count()} for ticker ${s.symbol}")
      }
      println(s"Done with $e")
    }

    println("Done")
  }
}
