package ib.data.stock

import ib.data.Ticker

import scala.io.Source

/**
  * Created by qili on 2015/9/10.
  */

//case class Stock(ticker: String, comment: String = "")

object StockRegistry {
  //  val all = Seq(Stock("IBM"), Stock("AAPL"), Stock("GOOG"), Stock("MSFT"), Stock(".IXIC", "Nasdaq composite Index"), Stock("ADBE"), Stock("SAP"),
  //    Stock("ORCL"))
  val stockFile = "/Users/qili/finance/ticker.csv"
  val all = {
    Source.fromFile(stockFile).getLines().toSeq.tail.map(Ticker(_))

  }
}
