package ib.data.crawler

import java.io._
import java.net.{URL, URLConnection}
import java.util.Date

import ib.Env
import ib.cassandra.TickerQuote
import ib.common.Loggable
import ib.data.{Quotes, Quote}
import ib.data.sink.{CassandraQuoteSaver, ISave, FileUtil, FileSaver}
import ib.util.DurationUtil

import scala.concurrent.duration.Duration

object SaveType extends Enumeration {
  val Cassandra, File = Value
}

import SaveType._

/**
  * Created by Ken on 2015/9/10.
  */
class GoogleCrawler(filePath: String, saveType: SaveType.Value = Cassandra) extends ICrawler with Loggable {

  val template: String = """http://www.google.com/finance/getprices?i=%s&p=%sd&f=d,o,h,l,c,v&df=cpct&q=%s"""

  def getUrl(ticker: String, duration: Duration, periods: Int): String = {
    template.format(DurationUtil.duration2String(duration), periods + "d", ticker)
  }

  def run(ticker: String, duration: Duration, periods: Int): Boolean = {
    System.setProperty("user.timezone", "America/New_York")

    val saver: ISave[TickerQuote] = saveType match {
      case Cassandra => {
        implicit val env = Env.DEV
        new CassandraQuoteSaver
      }
      case _ => {
        val file = (filePath + ticker + ".txt")
        new FileSaver[TickerQuote](file, (f, q) => {
          val ticker = f.split("/").last.replace(".txt", "")
          FileUtil.lastLine(file) match {
            case Some(s) => q.date.after(TickerQuote(ticker, s).date)
            case _ => true
          }
        })
      }
    }
    if (saver.updateToday(ticker)) {
      logger.info(s"$ticker has been updated today, ignore bothering Google")
      true
    } else {

      val url = getUrl(ticker, duration, periods)
      println(s"Downloading ", url)
      val conn: URLConnection = new URL(url).openConnection()

      // open the stream and put it into BufferedReader
      val br: BufferedReader = new BufferedReader(
        new InputStreamReader(conn.getInputStream()))

      var input = br.readLine()
      //skip the header
      while (null != input && !input.startsWith("a144")) input = br.readLine()

      if (null == input) {
        logger.error(s"There is no data for $ticker - url[$url]")
      } else {
        //Now saving the valuable data
        var date: Long = input.split(",").apply(0).substring(1).toLong * 1000
        input = br.readLine()
        var list = new scala.collection.mutable.MutableList[TickerQuote]
        while (null != input) {
          val line = input.split(",")
          val tag = line.apply(0)
          var dateTime: Long = date
          if (tag.startsWith("a144")) {
            println("reset the date!")
            date = tag.substring(1).toLong * 1000
            dateTime = date
          } else {
            dateTime = date + line.apply(0).toInt * 60 * 1000
          }

          val close = line.apply(1).toDouble
          val high = line.apply(2).toDouble
          val low = line.apply(3).toDouble
          val open = line.apply(4).toDouble
          val volume = line.apply(5).toDouble

          val quote = TickerQuote(ticker, new Date(dateTime), open, close, high, low, volume)

          list += quote

          input = br.readLine()
        }
        val count = saver.saveAll(list)
        logger.info(s"Saved $count new quotes for $ticker")
      }

      saver.close
      beNiceToGoogle
      true
    }


  }

  def beNiceToGoogle = {
    val sleepSec = ((Math.random() * 2 + 1) * 1000).toInt
    println(s"sleeping for $sleepSec millisecs...")
    Thread.sleep(sleepSec)
  }

}
