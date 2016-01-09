package ib.data.loader

import ib.data.stock.{Ticker, StockRegistry}
import ib.data.{Quotes, TimeSeries}

/**
  * Created by qili on 2015/9/11.
  */
class StockLoader(filtPath: String) extends ILoader {

  def getFile(stock: Ticker) = filtPath + "/" + stock.symbol + "-Percent.txt"

  def load(stock: Ticker) = {
    val f = getFile(stock)
    println(s"Loading stock file from $f")
    scala.io.Source.fromFile(f).getLines map (Quotes.parse(_)) map (q => TimeSeries(q.date, q.open)) toSeq
  }

  def loadAll: List[TimeSeries] = {
    StockRegistry.all.flatMap(load(_)).toList
  }
}



