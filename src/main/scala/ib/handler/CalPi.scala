package ib.handler

import ib.data.Pi
import ib.data.formatter.JsonFormatter._
import org.apache.spark.{SparkConf, SparkContext}
import spray.json._

/**
 * Created by qili on 23/08/2015.
 */
class CalPi extends Handler {
  @Override
  def process: String = {
    import ib.spark.Spark._
    val n = 9999999
    val pi: Double = (sc.parallelize(1 to n, 2) map {
      i => {
        val x = Math.random * 2 - 1
        val y = Math.random * 2 - 1
        if (x * x + y * y <= 1) 1 else 0
      }
    } reduce (_ + _)) * 4.0 / n
    println(s"The calculated Pi is $pi")

    val res = Pi(pi)
    res.toJson.prettyPrint
  }
}
