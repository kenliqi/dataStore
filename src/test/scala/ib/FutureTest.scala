package ib

import java.util.concurrent.Executors

import org.junit.Test

import scala.concurrent._

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by qili on 15/11/15.
  */
class FutureTest {
  //    implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(3))

  def heaveyComputer = {
    val i = (Math.random() * 100000).toLong
    println(s"sleeping for $i ms")
    if (i > 90000) throw new Exception(s"Too heavey calculation will take longer than $i ms")
    Thread.sleep(i)
    i
  }

  @Test
  def futureTest: Unit = {


    val f1 = Future {
      heaveyComputer
    }

    val f2 = Future {
      heaveyComputer
    }

    println("Started f1 and f2")

    f1 onComplete {
      case Success(i: Long) => println("Callback done for f1")
      case Failure(ex) => println(s"f1 failed with exception - $ex")
    }

    f2 onSuccess { case i => println(s"f2 is done with $i") }

    println("registered the callbacks for f1 and f2")

    Await.result(f1, 2 minutes)
    println("Done with waiting f1")
    Await.result(f2, 2 minutes)
    println("Done with waiting f2")
  }

  @Test
  def futureTest2 = {
    val f1 = Future {
      heaveyComputer
    }
    val f2 = Future {
      heaveyComputer
    }

    val res = for {i1 <- f1
                   i2 <- f2}
      yield (i1, i2)

    Await.result(res, 60 seconds)
    println(s"result is $res")
  }

  @Test
  def promiseTest = {
    val p = Promise[Int]
    val f = p.future
    val f1 = Future {
      val i = heaveyComputer
      println(s"Future1 is completed with $i ms")
      p success i.toInt
    }
    val f2 = Future {
      println("Wait for the f1 to complete")
      f onSuccess {
        case i => println(s"I got it, f1 is completed with $i ms")
      }
    }

    f1 onFailure { case e => println("f1 got exception ") }
    f1 onSuccess { case i => println(s"f1 got success $i") }

    Await.result(f1, 60 seconds)

  }

}
