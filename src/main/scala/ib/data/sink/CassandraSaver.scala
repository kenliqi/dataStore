package ib.data.sink

import java.util.Date

import ib.Env
import ib.cassandra.{Quote}
import ib.common.Loggable
import ib.data.Generic
import ib.data.stock.Ticker
import ib.spark.Spark._
import com.datastax.spark.connector._
import ib.util.DateUtil
import org.joda.time.DateTime


/**
  * Created by qili on 22/11/2015.
  */
class CassandraQuoteSaver(implicit env: Env.Value) extends ISave[Quote] with Generic with Loggable {
  def all = sc.cassandraTable(env, classOf[Quote])

  override def hasThisDay(ticker: Ticker, date: Date): Boolean = {
    val count = all.select("date").where("ticker = ? and exchange = ? and  day = ?", ticker.symbol, ticker.exchange, DateUtil.DATE.format(date)).limit(1).count()
    count > 0
  }

  override def updateToday(ticker: Ticker): Boolean = {
    val last = DateUtil.DATE.format(lastUpdate(ticker))
    val today = DateUtil.DATE.format(DateTime.now().toDate)
    last >= today
  }

  def lastUpdate(ticker: Ticker): Date = {
    lastUpdate(ticker.symbol, ticker.exchange.name())
  }

  def lastUpdate(symbol: String, exchange: String) = {
    val data = all.select("date").where("ticker = ? and exchange = ?", symbol, exchange).limit(1)
    if (data.count() > 0) data.first.getDate("date")
    else DateUtil.EdenTime
  }

  override def save(data: Quote): Boolean = {
    val last = lastUpdate(data.ticker, data.exchange)
    if (data.date.after(last)) {
      sc.parallelize(Seq(data)).saveToCassandra(env, classOf[Quote], Quote.allColumns)
      true
    } else false
  }

  override def saveAll(data: Seq[Quote]): Int = {
    var count = 0
    data.groupBy(_.ticker) foreach {
      case (ticker, d) => {
        //        val last = lastUpdate(ticker)
        //        val filterData = d.filter(_.date.after(last))
        sc.parallelize(d).saveToCassandra(env, classOf[Quote], Quote.allColumns)
        count = count + d.size
        logger.info(s"Saved ${d.size} quotes for $ticker")
      }
    }
    count
  }

  override def close: Unit = logger.info("close the cassandra connection")
}
