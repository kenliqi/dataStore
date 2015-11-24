package ib.data

import com.datastax.driver.core.ConsistencyLevel
import com.datastax.spark.connector.rdd.ReadConf
import ib.Env
import org.apache.spark.SparkContext
import org.apache.spark.sql.cassandra.CassandraSQLContext

/**
  * Created by qili on 24/11/2015.
  */
//This is the table name convention between scala case class and Cassandra table
trait Generic {
  implicit def envToString(e: Env.Value) = e.toString.toLowerCase

  implicit def classToString(c: Class[_]) = c.getName.replace('.', '_').toLowerCase()

  implicit def sparkToCassandraContext(sc: SparkContext): CassandraSQLContext = {
    new CassandraSQLContext(sc)
  }

  implicit val readConf = ReadConf(splitCount = Some(2), consistencyLevel = ConsistencyLevel.LOCAL_ONE) //As we only run locally, there are just 2 cores, so we create 2 partitions, others option as below,

}
