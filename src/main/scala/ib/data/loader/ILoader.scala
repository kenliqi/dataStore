package ib.data.loader

import ib.data.TimeSeries
import ib.data.stock.Ticker

/**
  * Created by qili on 2015/9/11.
  */
trait ILoader {
  def load(stock: Ticker): Seq[TimeSeries]
}
