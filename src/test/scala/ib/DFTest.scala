package ib

import org.apache.spark.sql.{SaveMode, DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}
import org.junit.Test
import com.datastax.spark.connector._

/**
  * Created by qili on 25/11/2015.
  */


class DFTest {

  val CassandraSeed = "localhost"
  val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("Test").set("spark.cassandra.connection.host", CassandraSeed)
    .set("ClusterOne/spark.cassandra.input.split.size_in_mb", "32")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  @Test
  def createTable = {
    val p = Articles("Ken", Seq("test"))
    sc.parallelize(Seq(p)).saveAsCassandraTable("test", "articles")
    println("done")
  }

  @Test
  def add = {
    val p2 = Articles("Jona", Seq("dummy"))
    import sqlContext.implicits._
    val df = sc.parallelize(Seq(p2)).toDF

    df.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "articles", "keyspace" -> "test"))
      .mode(SaveMode.Append)
      .save
  }

  @Test
  def query = {


    val df: DataFrame = sqlContext.read.format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "articles", "keyspace" -> "test"))
      .load()

    df.filter(df("name") === "Ken").show
    df.filter("name = 'Ken'").show
    df.explain

    df.show

  }

  @Test
  def append = {
    val p2 = Articles("Ken", Seq("Append"))
    import sqlContext.implicits._
    val df = sc.parallelize(Seq(p2)).toDF

    df.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "articles", "keyspace" -> "test"))
      .mode(SaveMode.Append)
      .save
  }

}
