package ib.data

import java.io.{PrintWriter, File}

import ib.Env
import ib.data.sink.CassandraQuoteSaver
import ib.data.stock.StockRegistry
import ib.spark.Spark

/**
  * Created by qili on 12/12/2015.
  */
object DataVerification extends Generic {
  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try {
      op(p)
    } finally {
      p.close()
    }
  }

  def main(args: Array[String]) {
    implicit val env = Env.DEV

    val allTickers = Spark.sc.parallelize(StockRegistry.all)
    val saver = new CassandraQuoteSaver()
    val file = new File("LastUpdate.csv")
    val output = new PrintWriter(file)
    println(s"Start processing ${allTickers.count()} tickers")
    allTickers.collect().map(t => t.symbol).map(n => (n, saver.lastUpdate(n))).foreach {
      l => output.append(s"${l.toString()}\n")
    }
    println(s"Done")
    output.close()

  }
}
