package ib

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import org.junit.Test

/**
  * Created by qili on 25/11/2015.
  */
class DFTest {

  @Test
  def DFSave = {
    val CassandraSeed = "localhost"
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("Test").set("spark.cassandra.connection.host", CassandraSeed)
      .set("ClusterOne/spark.cassandra.input.split.size_in_mb", "32")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    val df: DataFrame = sqlContext.read.format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "ib_data_person", "keyspace" -> "dev"))
      .load()

    df.explain

    df.show(1)

  }

}
