package ib.data.stock

import java.io.{File, FileOutputStream, PrintStream}

import ib.data.Exchange
import ib.util.ConversionUtil._

import scala.io.Source
import scala.util.Try

/**
  * Created by qili on 21/11/2015.
  *
  * mandatory fields: symbol, name, exchange
  *
  * others are optional as different exchanges may disclose different information
  */

case class Ticker(symbol: String, name: String, exchange: Exchange, marketCap: Option[Double], IPOYear: Option[Int],
                  sector: Option[String], industry: Option[String], summary: Option[String]) {

  override def toString = {
    val data = Seq(symbol, name, exchange.toString, marketCap.map(_.toString).getOrElse(""),
      IPOYear.map(_.toString).getOrElse(""),
      sector.getOrElse(""), industry.getOrElse(""), summary.getOrElse(""))
    data.mkString(",")
  }
}

object Ticker {
  def header = "symbol, name, exchange, marketCap, IPOYear, sector, industry, summary"

  def apply(s: String): Ticker = {
    val data = s.split(",", -1)
    assert(data.size >= 8, s"inValid ticker - $s")
    Ticker(data.apply(0), data.apply(1), Exchange.valueOf(data.apply(2)),
      Try(data.apply(3).toDouble), Try(data.apply(4).toInt), Try(data.apply(5)), Try(data.apply(6)), Try(data.apply(7)))
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
    Exchange.values() foreach { exch => {
      val ex = new File(s"$path/${exch.name}.csv")
      println(s"processing exchange $exch - file $ex")
      if (ex.exists() && ex.isFile) {
        val lines = Source.fromFile(ex)("ISO-8859-1").getLines().toSeq
        val header = lines.head.split(",").map(stripDoubleQuote(_)).map(_.toLowerCase)
        val tickers: Seq[Ticker] = lines.tail.map(l => {
          val newLine = if (l.startsWith("\"")) l else l.split(',').map(q => "\"" + q + "\"").mkString(",")
          val data = newLine.split("\",\"").map(stripDoubleQuote(_))
          val zippedData = header zip data toMap
          val symbol = zippedData.get("symbol").getOrElse(throw InvalidDataException(s"The symbol is not valid - $l"))
          val name = zippedData.get("name").getOrElse("")
          val marketCap = zippedData.get("marketcap").flatMap(toDouble(_))
          val ipoYear = zippedData.get("ipoyear").flatMap(toInt(_))
          val sector = zippedData.get("sector")
          val industry = zippedData.get("industry")
          val summary = zippedData.get("summary quote")
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




