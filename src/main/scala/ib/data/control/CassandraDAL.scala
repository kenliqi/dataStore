package ib.data.control

/**
 * Created by qili on 25/08/2015.
 */

import com.datastax.spark.connector._
import ib.spark.Spark._

import scala.reflect.ClassTag

class CassandraDAL(val keySpace: String) extends DAL {
  def createSchema(e: Class[_]): Boolean = {
    val table = e.getName
    val t = sc.cassandraTable(keySpace, table)
    true
  }

  def persist[T: ClassTag](e: T): T = {
    e
  }

  def query[T: ClassTag](q: String): Seq[T] = {
    Seq.empty
  }

}

object CassandraDAL {
  val QA = new CassandraDAL("QA")
  val RROD = new CassandraDAL("PROD")
  val DEV = new CassandraDAL("DEV")
}
