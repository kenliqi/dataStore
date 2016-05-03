package ib.data.crawler

import java.io._
import java.net.{URL, URLConnection}
import java.util
import java.util.Date

import ib.Env
import ib.cassandra.Quote
import ib.common.{Retry, Loggable}
import ib.data.stock.Ticker
import ib.data.{Exchange}
import ib.data.sink.{CassandraQuoteSaver, ISave, FileUtil, FileSaver}
import ib.util.{DateUtil, DurationUtil}

import scala.concurrent.duration.Duration

object SaveType extends Enumeration {
  val Cassandra, File = Value
}

import SaveType._

/**
  * Created by Ken on 2015/9/10.
  */
class GoogleCrawler(filePath: String, saveType: SaveType.Value = Cassandra, forceDownload: Boolean = false) extends ICrawler with Loggable with Retry {

  val withExchange = Set(Exchange.SHA, Exchange.SHE)

  val dbTickerDaySnapshot = new util.HashMap[(Ticker, String), Boolean]()

  val ConnectionTimeOut = 60 * 1000
  //60 seconds
  val ReadTimeOut = 120 * 1000 //120 seconds to retrieve the data

  def hasTickerDay(ticker: Ticker, date: Date): Boolean = {
    val key = (ticker, DateUtil.DATE.format(date))
    if (!dbTickerDaySnapshot.containsKey(key)) {
      //first time checking Database where it's saved or not
      val hasThisDay = tickerSaver(ticker).hasThisDay(ticker, date)
      dbTickerDaySnapshot.put(key, hasThisDay)
    }
    dbTickerDaySnapshot.get(key)
  }

  val template: String = """http://www.google.com/finance/getprices?i=%s&p=%sd&f=d,o,h,l,c,v&df=cpct&q=%s&x=%s"""
  val noExTemplate: String = """http://www.google.com/finance/getprices?i=%s&p=%sd&f=d,o,h,l,c,v&df=cpct&q=%s"""

  def getUrl(ticker: Ticker, duration: Duration, periods: Int): String = {
    if (withExchange.contains(ticker.exchange)) {
      template.format(DurationUtil.duration2String(duration), periods + "d", ticker.symbol, ticker.exchange.urlName)
    } else {
      noExTemplate.format(DurationUtil.duration2String(duration), periods + "d", ticker.symbol)
    }

  }

  def tickerSaver(ticker: Ticker): ISave[Quote] = saveType match {
    case Cassandra => {
      implicit val env = Env.DEV
      new CassandraQuoteSaver
    }
    case _ => {
      val file = (filePath + ticker.symbol + ".txt")
      new FileSaver[Quote](file, (f, q) => {
        //        val ticker = f.split("/").last.replace(".txt", "")
        FileUtil.lastLine(file) match {
          case Some(s) => q.date.after(Quote(ticker.symbol, ticker.exchange.name(), s).date)
          case _ => true
        }
      })
    }
  }

  def run(ticker: Ticker, duration: Duration, periods: Int): Boolean = {
    System.setProperty("user.timezone", "America/New_York")

    val RetryTimes = 3

    val saver = tickerSaver(ticker)

    if (!forceDownload && saver.updateToday(ticker)) {
      logger.info(s"$ticker has been updated today, ignore bothering Google")
      true
    } else {
      retry(RetryTimes)(realRun(ticker, duration, periods, saver))
    }

  }

  def realRun(ticker: Ticker, duration: Duration, periods: Int, saver: ISave[Quote]): Boolean = {
    val url = getUrl(ticker, duration, periods)
    println(s"Downloading ", url)
    val conn: URLConnection = new URL(url).openConnection()

    conn.setConnectTimeout(ConnectionTimeOut)
    conn.setReadTimeout(ReadTimeOut)

    // open the stream and put it into BufferedReader
    val br: BufferedReader = new BufferedReader(
      new InputStreamReader(conn.getInputStream()))

    var input = br.readLine()
    //skip the header
    while (null != input && !input.startsWith("a1")) input = br.readLine()

    if (null == input) {
      logger.error(s"There is no data for $ticker - url[$url]")
    } else {
      //Now saving the valuable data
      var date: Long = input.split(",").apply(0).substring(1).toLong * 1000
      input = br.readLine()
      val list = new java.util.ArrayList[Quote]
      while (null != input) {
        val line = input.split(",")
        val tag = line.apply(0)
        var dateTime: Long = date
        if (tag.startsWith("a1")) {
          println("reset the date!")
          date = tag.substring(1).toLong * 1000
          dateTime = date
        } else {
          dateTime = date + line.apply(0).replaceAll("[a-zA-Z]", "").toInt * 60 * 1000
        }


        val close = line.apply(1).toDouble
        val high = line.apply(2).toDouble
        val low = line.apply(3).toDouble
        val open = line.apply(4).toDouble
        val volume = line.apply(5).toDouble
        val datetime = new Date(dateTime)

        val quote = Quote(ticker.symbol, ticker.exchange.name(), DateUtil.DATE.format(datetime), datetime, open, close, high, low, volume)

        if (!hasTickerDay(ticker, quote.date)) {
          list.add(quote)
        }

        input = br.readLine()
      }
      import scala.collection.JavaConversions._
      val count = saver.saveAll(list)
      logger.info(s"Saved $count new quotes for $ticker")
    }

    saver.close
    beNiceToGoogle
    true
  }

  def beNiceToGoogle = {
    val sleepSec = ((Math.random() * 2 + 1) * 1000).toInt
    println(s"sleeping for $sleepSec millisecs...")
    Thread.sleep(sleepSec)
  }

}
