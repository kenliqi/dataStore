package ib.data

import ib.data.stock.StockRegistry
import ib.util.ConversionUtil

import scala.io.Source
import java.io.{FileOutputStream, PrintStream, File}
import scala.util.Try
import ConversionUtil._

/**
  * Created by qili on 21/11/2015.
  */

case class Ticker(symbol: String, name: String, exchange: Exchange, marketCap: Option[Double], IPOYear: Option[Int],
                  sector: String, industry: String, summary: String) {

  override def toString = {
    val data = Seq(symbol, name, exchange.toString, marketCap.map(_.toString).getOrElse(""),
      IPOYear.map(_.toString).getOrElse(""),
      sector, industry, summary)
    data.mkString(",")
  }
}

object Ticker {
  def header = "symbol, name, exchange, marketCap, IPOYear, sector, industry, summary"

  def apply(s: String): Ticker = {
    val data = s.split(",")
    assert(data.size >= 8)
    Ticker(data.apply(0), data.apply(1), Exchange.valueOf(data.apply(2)),
      Try(data.apply(3).toDouble), Try(data.apply(4).toInt), data.apply(5), data.apply(6), data.apply(7))
  }
}

case class InvalidDataException(msg: String) extends Exception(msg)

object TickerLoader {



  val path = "/Users/qili/finance/refdata"

  def main(args: Array[String]) {
    val tickerFile = new File(StockRegistry.stockFile)
    tickerFile.createNewFile()
    val fos = new PrintStream(new FileOutputStream(tickerFile))
    fos.append(Ticker.header + "\n")
    val exchanges = (new File(path)).listFiles()
    exchanges foreach { ex => {
      if (ex.exists() && ex.isFile) {
        val exch = Exchange.valueOf(ex.getName.stripSuffix(".csv"))
        val lines = Source.fromFile(ex).getLines().toSeq
        val header = lines.head.split(",").map(stripDoubleQuote(_)).map(_.toLowerCase)
        val tickers: Seq[Ticker] = lines.tail.map(l => {
          val data = l.split("\",\"").map(stripDoubleQuote(_))
          val zippedData = header zip data toMap
          val symbol = zippedData.get("symbol").getOrElse(throw InvalidDataException(s"The symbol is not valid - $l"))
          val name = zippedData.get("name").getOrElse("")
          val marketCap = zippedData.get("marketcap").flatMap(toDouble(_))
          val ipoYear = zippedData.get("ipoyear").flatMap(toInt(_))
          val sector = zippedData.get("sector").getOrElse("")
          val industry = zippedData.get("industry").getOrElse("")
          val summary = zippedData.get("summary quote").getOrElse("")
          Ticker(symbol, name, exch, marketCap, ipoYear, sector, industry, summary)
        })
        tickers foreach { t => fos.append(t.toString + "\n") }
        println(s"Done with exchange[$ex] with [${tickers.size}] tickers")
      }

    }
    }
    fos.close()

  }

  def stripDoubleQuote(s: String) = s.replaceAll("\"|,", "").replace("^", "-")

  def toDouble(s: String): Option[Double] = if (s.contains("K")) Some(s.substring(1, s.length - 1).toDouble * 1000)
  else if (s.contains("M")) Some(s.substring(1, s.length - 1).toDouble * 1000 * 1000)
  else if (s.contains("B")) Some(s.substring(1, s.length - 1).toDouble * 1000 * 1000 * 1000)
  else Try(s.toDouble)

  def toInt(s: String): Option[Int] = Try(s.toInt)
}




