package ib.spark

import com.datastax.spark.connector.cql.CassandraConnector
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by qili on 25/08/2015.
 */
object Spark {

  val SparkCleanerTtl = 60 * 1000
  val SparkMaster = "local[2]"
  val CassandraSeed = "localhost"
  val KeepAliveMs = 10 * 60 * 1000

  val conf = new SparkConf(true).setAppName("IB computing service")
    .set("spark.cassandra.connection.host", CassandraSeed)
    .set("spark.cleaner.ttl", SparkCleanerTtl.toString)
    .set("spark.cassandra.connection.keep_alive_ms", KeepAliveMs.toString)
    .setMaster(SparkMaster)
  implicit lazy val sc = {
    //    conf.set("spark.master", "local[2]")
    new SparkContext(conf)
  }

  implicit lazy val connector = CassandraConnector(conf)

}
