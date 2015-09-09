package ib.data

import ib.Env
import ib.annotation.{entity, index, partitionKey}
import org.apache.spark.SparkContext
import org.apache.spark.sql.cassandra.CassandraSQLContext
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

  implicit def sparkToCassandraContext(sc: SparkContext): CassandraSQLContext = {
    new CassandraSQLContext(sc)
  }
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
//    User.all.filter(_.name=="Jill").foreach(println)
//  }
//
//}
//
//

