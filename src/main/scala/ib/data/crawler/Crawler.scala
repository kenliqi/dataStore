package ib.data.crawler

import ib.data.Exchange
import ib.data.stock.StockRegistry
import scala.concurrent.duration._


case class CrawlerArgs(days: Int = 1, forceDownload: Boolean = false, exchanges: List[Exchange] = Exchange.values().toList)



/**
  * Created by Ken on 2015/9/10.
  *
  * this is snapping all the financial data we need
  */
object Crawler {

  implicit val exchangeRead: scopt.Read[List[Exchange]] =
    scopt.Read.reads(exs => {
      val ex = exs.split(",")
      if (ex.isEmpty) Exchange.values().toList
      else ex.map(Exchange.valueOf(_)).toList
    })

  val crawlerArgsParser = new scopt.OptionParser[CrawlerArgs]("Crawler") {
    head("Stock quotes crawler")
    opt[Int]('d', "days") action { (days, config) => config.copy(days = days) } text ("how many days the crawler will look backwards")
    opt[Unit]('f', "forceDownload") action { (_, config) => config.copy(forceDownload = true) } text ("force download the market data no matter we check it's up to date or not")
    opt[List[Exchange]]('e', "exchanges") action { (ex, config) => config.copy(exchanges = ex) } text ("the exchanges we will crawl")
  }
  val FalseSet = Set("false", "f")
  def main(args: Array[String]) {
    import CrawlerMode._

    val cArgs = crawlerArgsParser.parse(args, CrawlerArgs()) match {
      case Some(cArgs) => cArgs
      case None => {
        crawlerArgsParser.showUsage
        throw new Exception(crawlerArgsParser.usage)
      }
    }

    val days = cArgs.days

    val forceDownload = cArgs.forceDownload

    //    val exchange = if(args.length >=3)
    //    val mode = if (args.length < 1) Daily else CrawlerMode.withName(args.apply(0))
    val filePath = "/Users/qili/finance/data/"

    println(s"Crawling mode $days, files saved to $filePath, exchanges ${cArgs.exchanges}")
    for (stock <- StockRegistry.all.filter(ticker => cArgs.exchanges.contains(ticker.exchange))) {
      println(s"Snapping the stock $stock")
      val crawler = new GoogleCrawler(filePath, SaveType.Cassandra, forceDownload)
      crawler.run(stock.symbol, 60.seconds, days)
    }
  }
}

object CrawlerMode extends Enumeration {
  val Daily, Yesterday, Batch = Value
}

object SingleTickerCrawler {
  def main(args: Array[String]) {
    val ticker = "EPD"
    val crawler = new GoogleCrawler("dummy", SaveType.Cassandra, true)
    crawler.run(ticker, 60.seconds, 4)
  }
}
