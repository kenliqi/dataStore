package ib.data.stock.impl

import java.io._
import java.net.{URL, URLConnection}
import java.util.Date

import ib.data.Quote
import ib.data.stock.ICrawler
import ib.util.DurationUtil

import scala.concurrent.duration.Duration

/**
  * Created by Ken on 2015/9/10.
  */
class GoogleCrawler(filePath: String) extends ICrawler {

  val template: String = """http://www.google.com/finance/getprices?i=%s&p=%sd&f=d,o,h,l,c,v&df=cpct&q=%s"""

  def getUrl(ticker: String, duration: Duration, periods: Int): String = {
    template.format(DurationUtil.duration2String(duration), periods + "d", ticker)
  }

  def run(ticker: String, duration: Duration, periods: Int): Boolean = {
    System.setProperty("user.timezone", "America/New_York")

    val file = new File(filePath + ticker + ".txt")
    if (!file.exists()) {
      file.createNewFile()
    }


    val fos = new PrintStream(new FileOutputStream(file))


    val url = getUrl(ticker, duration, periods)
    println(s"Downloading ", url)
    val conn: URLConnection = new URL(url).openConnection()

    // open the stream and put it into BufferedReader
    val br: BufferedReader = new BufferedReader(
      new InputStreamReader(conn.getInputStream()))

    var input = br.readLine()
    //skip the header
    while (null != input && !input.startsWith("a144")) input = br.readLine()
    //Now saving the valuable data
    var date: Long = input.split(",").apply(0).substring(1).toLong * 1000
    input = br.readLine()
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

      val quote = Quote(new Date(dateTime), open, close, high, low, volume)

      fos.append(quote.toString)
      fos.append('\n')
      input = br.readLine()
    }
    fos.close()
    beNiceToGoogle
    true
  }

  def beNiceToGoogle = {
    val sleepSec = (Math.random() * 100 + 5).toInt
    println(s"sleeping for $sleepSec seconds...")
    Thread.sleep(sleepSec * 1000)
  }

}
