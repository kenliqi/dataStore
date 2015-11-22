package ib

import ib.cassandra.TickerQuote
import ib.data.Quotes
import ib.data.sink.CassandraQuoteSaver
import org.junit.Test

/**
  * Created by qili on 22/11/2015.
  */
class CassandraQuoteSaverTest {

  @Test
  def getQuote = {
    implicit val env = Env.DEV
    val saver = new CassandraQuoteSaver
    val lastUpdate = saver.lastUpdate("AAPL")
    val q = Quotes.parse("2015-11-02 14:31:01,119.83,119.68,119.87,119.68,160218.0")
    val tickerQ = TickerQuote("AAPL", q.date, q.open, q.close, q.high, q.low, q.volume)
    saver.saveAll(Seq(tickerQ))
  }
}
