package ib.data

import com.sun.tools.javac.code.TypeTag
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

//This is the table name convention between scala case class and Cassandra table
trait Generic {
  implicit def envToString(e: Env.Value) = e.toString.toLowerCase

  implicit def classToString(c: Class[_]) = c.getName.replace('.', '_').toLowerCase()
}


//TODO: Can we generate the following code automatically for developers?
object Persons extends Generic {
  def all(implicit env: Env.Value) = sc.cassandraTable[Person](env, classOf[Person])
}

//
//trait Entity[T] {
//  def all = sc.cassandraTable(T)("test", classOf[T].getName)
//}
//
//class User(val name:String, val addr:String)
//  object User extends Entity[User] {
//    def all2 = sc.cassandraTable[User]("test", classOf[User].getName)
//  }
//
//
//
//object entityStore extends Generic{
//
//  def main(args: Array[String]) {
//    val u = new User("ken", "test")
//    User.all.filter(_.name=="Jill").foreach(println)
//  }
//
//}
//
//

