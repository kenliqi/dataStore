package ib.data.crawler

import ib.data.stock.StockRegistry
import scala.concurrent.duration._

/**
  * Created by Ken on 2015/9/10.
  *
  * this is snapping all the financial data we need
  */
object Crawler {
  def main(args: Array[String]) {
    import CrawlerMode._
    val mode = if (args.length < 1) Daily else CrawlerMode.withName(args.apply(0))
    val filePath = "/Users/qili/finance/data/"

    println(s"Crawling mode $mode, files saved to $filePath")
    for (stock <- StockRegistry.all) {
      println(s"Snapping the stock $stock")
      val crawler = new GoogleCrawler(filePath)
      val days = mode match {
        case Daily => 1
        case Batch => 15 //Google allows at most 15 past days
      }
      crawler.run(stock.ticker, 60.seconds, days)
    }
  }
}

object CrawlerMode extends Enumeration {
  val Daily, Batch = Value
}
