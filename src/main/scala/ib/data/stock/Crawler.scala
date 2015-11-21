package ib.data.stock

import ib.data.stock.impl.GoogleCrawler

import scala.concurrent.duration._

/**
  * Created by Ken on 2015/9/10.
  *
  * this is snapping all the financial data we need
  */
object Crawler {
  def main(args: Array[String]) {
    val filePath = "/Users/qili/finance/data/"

    for (stock <- StockRegistry.all) {
      println(s"Snapping the stock $stock")
      val crawler = new GoogleCrawler(filePath)
      crawler.run(stock.ticker, 60.seconds, 10)
    }
  }
}
