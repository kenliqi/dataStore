package ib.handler

import ib.data.Pi
import org.apache.spark.{SparkConf, SparkContext}
import spray.json.DefaultJsonProtocol

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat1(Pi)
}

import ib.handler.MyJsonProtocol._
import spray.json._

/**
 * Created by qili on 23/08/2015.
 */
class CalPi extends Handler {
  @Override
  def process: String = {
    val conf = new SparkConf().setAppName("Calculate Pi")
    conf.set("spark.master", "local[2]")
    val spark = new SparkContext(conf)
    val n = 9999999
    val pi: Double = (spark.parallelize(1 to n, 2) map {
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
