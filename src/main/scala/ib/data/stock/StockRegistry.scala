package ib.data.stock

/**
  * Created by qili on 2015/9/10.
  */

case class Stock(ticker: String, comment: String = "")

object StockRegistry {
  val all = Seq(Stock("IBM"), Stock("AAPL"), Stock("GOOG"), Stock("MSFT"), Stock(".IXIC", "Nasdaq composite Index"), Stock("ADBE"), Stock("SAP"),
    Stock("ORCL"))
}
