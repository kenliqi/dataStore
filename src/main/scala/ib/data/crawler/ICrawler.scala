package ib.data.crawler

import ib.data.Exchange
import ib.data.stock.Ticker

import scala.concurrent.duration.Duration

/**
  * Created by Ken on 2015/9/10.
  */
trait ICrawler {
  val template: String

  def run(ticker: Ticker, duration: Duration, periods: Int): Boolean


}
