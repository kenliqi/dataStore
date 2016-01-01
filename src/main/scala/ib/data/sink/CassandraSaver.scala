package ib.data.sink

import java.util.Date

import ib.Env
import ib.cassandra.TickerQuote
import ib.common.Loggable
import ib.data.Generic
import ib.spark.Spark._
import com.datastax.spark.connector._
import ib.util.DateUtil
import org.joda.time.DateTime


/**
  * Created by qili on 22/11/2015.
  */
class CassandraQuoteSaver(implicit env: Env.Value) extends ISave[TickerQuote] with Generic with Loggable {
  def all = sc.cassandraTable(env, classOf[TickerQuote])

  override def hasThisDay(ticker: String, date: Date): Boolean = {
    val upperDateTime = new DateTime(date).plusHours(1)
    val count = all.select("date").where("ticker = ? and date >= ? and date < ?", ticker, date, upperDateTime.toDate).limit(1).count()
    count > 0
  }

  override def updateToday(ticker: String): Boolean = {
    val last = DateUtil.DATE.format(lastUpdate(ticker))
    val today = DateUtil.DATE.format(DateTime.now().toDate)
    last >= today
  }

  def lastUpdate(ticker: String): Date = {
    val data = all.select("date").where("ticker = ?", ticker).limit(1)
    if (data.count() > 0) data.first.getDate("date")
    else DateUtil.EdenTime
  }

  override def save(data: TickerQuote): Boolean = {
    val last = lastUpdate(data.ticker)
    if (data.date.after(last)) {
      sc.parallelize(Seq(data)).saveToCassandra(env, classOf[TickerQuote], TickerQuote.allColumns)
      true
    } else false
  }

  override def saveAll(data: Seq[TickerQuote]): Int = {
    var count = 0
    data.groupBy(_.ticker) foreach {
      case (ticker, d) => {
        val last = lastUpdate(ticker)
        val filterData = d.filter(_.date.after(last))
        sc.parallelize(filterData).saveToCassandra(env, classOf[TickerQuote], TickerQuote.allColumns)
        count = count + filterData.size
        logger.info(s"Saved ${filterData.size} quotes for $ticker")
      }
    }
    count
  }

  override def close: Unit = logger.info("close the cassandra connection")
}
