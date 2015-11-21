package ib.data.crawler

import scala.concurrent.duration.Duration

/**
  * Created by Ken on 2015/9/10.
  */
trait ICrawler {
  val template: String

  def run(ticker: String, duration: Duration, periods: Int): Boolean


}
