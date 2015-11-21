package ib.service

import akka.actor.{ActorLogging, Actor, ActorRef}
import ib.Env
import ib.data.Generic
import org.apache.spark.streaming.{Seconds, StreamingContext}
import ib.spark.Spark._
import com.datastax.spark.connector.streaming._
import com.datastax.spark.connector._

/**
 * Created by qili on 08/09/2015.
 *
 * refreshing the word count statistics in interval seconds
 *
 */

case class WordCount(word: String, count: Int)

object WordCounts extends Generic {
  def all(implicit env: Env.Value) = sc.cassandraTable[WordCount](env, classOf[WordCount])

}

object WordCountService extends Generic with IService {

  val port = 9999

  val sec = 1

  def run(implicit env: Env.Value) = {
    val ssc = new StreamingContext(sc, Seconds(sec))

    val lines = ssc.socketTextStream("localhost", port)

    val newRdd = lines.flatMap(_.split(' ')).map(word => (word, 1)).reduceByKey(_ + _).map { case (word, count) => WordCount(word, count) }

    val existingRdd = ssc.cassandraTable[WordCount](env, classOf[WordCount])

    //TODO: it's inner join, can we have outter join or left join here?
    val rdd = newRdd.joinWithCassandraTable(env, classOf[WordCount]).map { case (newWc, oldWc) => WordCount(newWc.word, newWc.count + oldWc.getInt("count")) }

    rdd.saveToCassandra(env, classOf[WordCount])

    ssc.start()
  }


}
