package ib.service

/**
 * Created by qili on 27/08/2015.
 */

import akka.actor.{ActorLogging, Actor, ActorRef}
import ib.spark.Spark._
import spray.can.Http
import spray.http._
import spray.http.MediaTypes._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import akka.actor._

class CountDownStream(responder: ActorRef, sec: Int) extends Actor with ActorLogging {

  private case class Timer(n: Int)

  private val header = (1 to 1024).map(_ => "\uFEFF").mkString("")

  self ! Timer(sec)
  responder ! ChunkedResponseStart(HttpResponse(entity = HttpEntity(ContentType(`text/plain`, HttpCharsets.`UTF-8`), header)))

  def receive = {
    case Timer(0) =>
      responder ! ChunkedMessageEnd()
      context.stop(self)

    case Timer(n) =>
      responder ! MessageChunk(s"$n seconds left\n")
      context.system.scheduler.scheduleOnce(1 second, self, Timer(n - 1))

    case ev: Http.ConnectionClosed =>
      log.debug("connection closed")
      context.stop(self)
  }
}
