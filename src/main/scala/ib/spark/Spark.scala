package ib.spark

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by qili on 25/08/2015.
 */
object Spark {

  val SparkCleanerTtl = 60 * 1000
  val SparkMaster = "local[2]"
  val CassandraSeed = "localhost"
  val KeepAliveMs = 60 * 1000

  implicit lazy val sc = {
    val conf = new SparkConf(true).setAppName("IB computing service")
      .set("spark.cassandra.connection.host", CassandraSeed)
      .set("spark.cleaner.ttl", SparkCleanerTtl.toString)
      .set("spark.cassandra.connection.keep_alive_ms", KeepAliveMs.toString)
      .setMaster(SparkMaster)
    //    conf.set("spark.master", "local[2]")
    new SparkContext(conf)
  }

}
