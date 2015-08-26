package ib.data

import ib.Env
import ib.annotation.{entity, index, partitionKey}
import spray.json._
import ib.data.formatter.JsonFormatter._
import com.datastax.spark.connector._
import com.datastax.spark.connector.rdd.CassandraRDD
import ib.spark.Spark._
/**
 * Created by qili on 25/08/2015.
 */
@entity
case class Person(@partitionKey val name: String, @index val school: String, val age: Int) extends toJsonString {
  def toJsonString: String = this.toJson.prettyPrint
}

trait Generic {
  implicit def envToString(e: Env.Value) = e.toString.toLowerCase

  implicit def classToString(c: Class[_]) = c.getName.replace('.', '_').toLowerCase()
}

object Persons extends Generic {
  def all(implicit env: Env.Value) = sc.cassandraTable[Person](env, classOf[Person])
}


