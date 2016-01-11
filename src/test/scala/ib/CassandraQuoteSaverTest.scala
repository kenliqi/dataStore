package ib

import ib.cassandra.{Quote, TickerQuote}
import ib.data.stock.StockRegistry
import ib.data.{Exchange, Quotes}
import ib.data.sink.CassandraQuoteSaver
import ib.util.DateUtil
import org.junit.Test

/**
  * Created by qili on 22/11/2015.
  */
class CassandraQuoteSaverTest {

  @Test
  def getQuote = {
    implicit val env = Env.DEV
    val saver = new CassandraQuoteSaver
    val ticker = StockRegistry.stock("AAPL", Exchange.NASDAQ).head
    val lastUpdate = saver.lastUpdate(ticker)
    val q = Quotes.parse("2015-11-02 14:31:01,119.83,119.68,119.87,119.68,160218.0")
    val tickerQ = Quote("AAPL", Exchange.NASDAQ.name(), q.date, q.open, q.close, q.high, q.low, q.volume)
    saver.saveAll(Seq(tickerQ))
  }

  @Test
  def hasTickerDay = {
    implicit val env = Env.DEV
    val saver = new CassandraQuoteSaver
    val ubs = StockRegistry.stock("UBS", Exchange.NYSE).head
    println(saver.hasThisDay(ubs, DateUtil.SDF.parse("2015-12-24 17:28:00+0000")))
    println(saver.hasThisDay(ubs, DateUtil.SDF.parse("2015-12-25 17:28:00+0000")))
    println(saver.hasThisDay(ubs, DateUtil.SDF.parse("2015-12-28 17:28:00+0000")))
    println(saver.hasThisDay(ubs, DateUtil.SDF.parse("2015-12-29 17:28:00+0000")))
  }

  @Test
  def run = {
    assert(1 == 2)
  }
}
