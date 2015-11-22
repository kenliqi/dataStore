package ib.data.sink

import java.util.Date

import ib.Env
import ib.cassandra.TickerQuote
import ib.common.Loggable
import ib.data.Generic
import ib.spark.Spark._
import com.datastax.spark.connector._
import ib.util.DateUtil


/**
  * Created by qili on 22/11/2015.
  */
class CassandraQuoteSaver(implicit env: Env.Value) extends ISave[TickerQuote] with Generic with Loggable {
  def all = sc.cassandraTable[TickerQuote](env, classOf[TickerQuote])

  def lastUpdate(ticker: String): Date = {
    val data = all.filter(_.ticker == ticker)
    if (data.count() > 0) data.first.date
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
