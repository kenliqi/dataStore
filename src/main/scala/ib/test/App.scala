package ib.test

import org.apache.spark.{SparkConf, SparkContext}


/**
 * Hello world!
 *
 */
object App {
  def main (args: Array[String]) {
    val conf = new SparkConf().setAppName("Calculate Pi")
    conf.set("spark.master", "local[2]")
    val spark = new SparkContext(conf)
    val n = 9999999
    val pi : Double = (spark.parallelize(1 to n, 2) map {
      i => {
        val x = Math.random * 2 -1
        val y = Math.random * 2 -1
        if(x*x+y*y<=1) 1 else 0
      }
    } reduce(_+_) )* 4.0 / n
    println(s"The Pi is $pi")
  }
}
