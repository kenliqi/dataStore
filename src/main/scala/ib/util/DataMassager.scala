package ib.util

import java.io.{FileOutputStream, PrintStream, File}

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.util.Timeout
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import spray.can.Http

import scala.io.Source

/**
  * Created by willrubens on 10/09/15.
  */

case class StockRow(date: DateTime, open: Double, close: Double, high: Double, low: Double, volume: Double)

object DataMassager extends App {

  val dateTimeFormat = DateTimeFormat.forPattern("YYY-MM-DD HH:mm:ss")

  val stocks = List("AAPL", "IBM", "GOOG", "MSFT", "ORCL", "SAP", "ADBE")
  //val stocks = List("ORCL", "SAP", "ADBE")

  stocks.foreach { stockname => {
    println(s"Massaging ${stockname}")
    val inputFile = s"data/$stockname.txt"
    val outputFileMN = s"data/$stockname-SentimentMN.txt"
    val outputFileP = s"data/$stockname-Percent.txt"

    val src = Source.fromFile(inputFile)
    val iter = src.getLines().map(
      csvLine => csvLine.split(",")
    ).toList
    val stockRow = iter map { (a: Array[String]) => StockRow(DateTime.parse(a(0), dateTimeFormat),
      a(1).toDouble, a(2).toDouble, a(3).toDouble, a(4).toDouble, a(5).toDouble)
    }

    val openStats = new SummaryStatistics()
    val closeStats = new SummaryStatistics()
    val highStats = new SummaryStatistics()
    val lowStats = new SummaryStatistics()

    var openTotal = 0.0
    var closeTotal = 0.0
    var highTotal = 0.0
    var lowTotal = 0.0
    var count = 0.0

    var openStart = stockRow(0).open
    var closeStart = stockRow(0).close
    var highStart = stockRow(0).high
    var lowStart = stockRow(0).low

    stockRow foreach (row => {
      openStats.addValue(row.open)
      closeStats.addValue(row.close)
      highStats.addValue(row.high)
      lowStats.addValue(row.low)

    })

    val openMean = openTotal / count
    val closeMean = closeTotal / count
    val highMean = highTotal / count
    val lowMean = lowTotal / count

    val meanNormalised = stockRow.map(row => StockRow(row.date,
      (row.open - openStats.getMean) / openStats.getStandardDeviation,
      (row.close - closeStats.getMean) / closeStats.getStandardDeviation,
      (row.high - highStats.getMean) / highStats.getStandardDeviation,
      (row.low - lowStats.getMean) / lowStats.getStandardDeviation,
      (row.volume)))

    val percentNormalised = stockRow.map(row => StockRow(row.date,
      row.open / openStart,
      row.close / closeStart,
      row.high / highStart,
      row.low / lowStart,
      row.volume))


    val meanNormalisedFile = new File(outputFileMN);
    if (!meanNormalisedFile.exists()) {
      meanNormalisedFile.createNewFile();
    }
    val percentFile = new File(outputFileP);
    if (!percentFile.exists()) {
      percentFile.createNewFile();
    }

    val mnFos = new PrintStream(new FileOutputStream(meanNormalisedFile))

    meanNormalised.foreach { row => mnFos.println(
      s"${row.date.toString(dateTimeFormat)}, ${row.open}, ${row.close}, ${row.high}, ${row.low}, ${row.volume}")
    }

    val percentFos = new PrintStream(new FileOutputStream(percentFile))

    percentNormalised.foreach { row => percentFos.println(
      s"${row.date.toString(dateTimeFormat)}, ${row.open}, ${row.close}, ${row.high}, ${row.low}, ${row.volume}")
    }
  }
  }

}