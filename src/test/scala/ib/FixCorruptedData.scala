package ib

import ib.cassandra.{QuoteLoading, TickerQuote}
import ib.common.Loggable
import ib.data.Generic
import com.datastax.spark._
import ib.spark.Spark

/**
  * Created by qili on 01/12/2015.
  */
object FixCorruptedData extends Generic with Loggable {

  def main(args: Array[String]) {
    implicit val env = Env.DEV
    val session = Spark.connector.openSession()
    val tickers = Seq("XXII", "FAX", "IAF", "CH", "ABE           ", "FCO", "IF", "ISL", "ACU", "AIII", "ATNM", "AE", "ADK", "ADK-A", "ACY", "WGA", "AIRI", "AXU", "AAU", "APT", "ALTV", "AAMC", "DIT", "ADGE", "AFCO", "ALN", "AMS", "AMPE", "APHB", "AXN", "AKG", "AINC", "AST", "AVL", "ASM", "BTG", "BTN", "BKJ", "BCV", "BAA", "BHB", "BRN", "BGSF", "BPMX", "BTX", "BGI", "BZM", "MHE", "BLE", "BLJ", "BFY", "BDR", "BRG", "BRG-A", "BVX", "BWL.A", "BZC", "BTI", "CANF", "ROX", "CAW", "CVM", "CEF", "GTU", "CET", "LEU", "CCF", "CQH", "CQP", "LNG", "CVR", "CNR", "CPHI", "CKX", "GLV", "GLQ", "GLO", "MOC", "LODE", "CTO", "MCF", "CMT", "CVRS", "CRMD", "CRF", "CLM", "CVU", "CIK", "DHY", "CRHM", "CRVP", "CVSL", "DAKP", "DXR", "VCF", "VFL", "VMM", "DLA", "DNN", "DGSE", "DPW", "DSS", "DMF", "DXI", "GRF", "ESTE", "EVM", "ISR", "ITI", "KLDX", "LTS-A", "LAQ", "LBMH", "LEI", "MVG", "MHH", "CCA", "MLSS", "MGN", "MZA")
    tickers foreach { t => {
      val quotes = QuoteLoading.all.where("ticker = ?", t).where("date >= ?", "2015-12-01").collect()
      for (q <- quotes) {
        logger.info(s"Deleting $q")
        val sql = "delete from dev.ib_cassandra_tickerquote where ticker=? and date=? ;"
        session.execute(sql, q.ticker, q.date)
      }
      logger.info(s"Done with ticker - $t")
    }
    }
    logger.info("Finished")
  }

}
