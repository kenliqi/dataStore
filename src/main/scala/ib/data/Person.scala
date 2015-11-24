package ib.data

import com.datastax.spark.connector._
import ib.Env
import ib.annotation.{entity, index, partitionKey}
import ib.spark.Spark._
import spray.json._


/**
 * Created by qili on 25/08/2015.
 */
@entity
case class Person(@partitionKey val name: String, @index val school: String, val age: Int) extends toJsonString {
  def toJsonString: String = this.toJson.prettyPrint
}

//TODO: Can we generate the following code automatically for developers?
object Persons extends Generic {
  def all(implicit env: Env.Value) = sc.cassandraTable[Person](env, classOf[Person])

  def save(persons: Seq[Person])(implicit env: Env.Value) = {
    val rdd = sc.parallelize(persons)
    if (!tableExists) {
      println("Creating the table " + env + "." + classOf[Person])
      rdd.saveAsCassandraTable(env, classOf[Person])
    }

    else
      rdd.saveToCassandra(env, classOf[Person])
  }

  //TODO: Currently the adaptor doesn't support this query, it will always assume this is query against table
  def tableExists(implicit env: Env.Value): Boolean = {
    //    val res = sc.sql("describe tables")
    //        !res.collect().contains(_.equals(classToString(classOf[Person])))
    true
  }

  def query(sql: String)(implicit env: Env.Value) = sc.sql(sql)
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
//    User.all.filter(_.name=="qili").foreach(println)
//  }
//
//}
//
//

