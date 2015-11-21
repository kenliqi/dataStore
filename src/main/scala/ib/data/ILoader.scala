package ib.data

import ib.data.stock.Stock

/**
  * Created by qili on 2015/9/11.
  */
trait ILoader {
  def load(stock: Stock): Seq[TimeSeries]
}
