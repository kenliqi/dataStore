package ib.data.stock

import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.{Channels, ReadableByteChannel}

import scala.concurrent.duration.Duration

/**
  * Created by Ken on 2015/9/10.
  */
trait ICrawler {
  val template: String

  def run(ticker: String, duration: Duration, periods: Int): Boolean


}
