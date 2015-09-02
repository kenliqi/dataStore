package ib.data.control

/**
 * Created by qili on 25/08/2015.
 */

import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraRDD
import ib.spark.Spark._

import scala.reflect.ClassTag

class CassandraDAL(val keySpace: String) extends DAL {
  def createSchema(e: Class[_]): Boolean = {
    val table = e.getName
    val t = sc.cassandraTable(keySpace, table)
    true
  }

  def persist[T](e: T)(implicit tag: ClassTag[T]): T = {
    e
  }

  def query[T](q: String)(implicit tag: ClassTag[T]): Seq[T] = {
    val table: String = tag.runtimeClass.getName
    val rdd: CassandraRDD[CassandraRow] = sc.cassandraTable(keySpace, table)

    //TODO: convert the CassandraRow into generic T
    rdd.collect().toSeq
    Seq.empty
  }

}

object CassandraDAL {
  val QA = new CassandraDAL("QA")
  val RROD = new CassandraDAL("PROD")
  val DEV = new CassandraDAL("DEV")
}
