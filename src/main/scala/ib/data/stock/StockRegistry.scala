package ib.data.stock

import ib.data.Exchange
import ib.data.stock.Ticker

import scala.io.Source

/**
  * Created by qili on 2015/9/10.
  */

//case class Stock(ticker: String, comment: String = "")

object StockRegistry {
  //  val all = Seq(Stock("IBM"), Stock("AAPL"), Stock("GOOG"), Stock("MSFT"), Stock(".IXIC", "Nasdaq composite Index"), Stock("ADBE"), Stock("SAP"),
  //    Stock("ORCL"))
  val stockFile = "/Users/qili/finance/NewTicker.csv"
  lazy val all = {
    Source.fromFile(stockFile).getLines().toSeq.tail.map(Ticker(_))

  }

  def exchange(ex: Exchange) = all.filter(_.exchange == ex)

  def stock(name: String, ex: Exchange) = all.filter(s => s.symbol == name && s.exchange == ex)


  def main(args: Array[String]) {
    all foreach println
  }
}
