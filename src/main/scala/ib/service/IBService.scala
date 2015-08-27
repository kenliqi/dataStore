package ib.service

import akka.actor.{ActorLogging, Props, Actor}
import ib.Env
import ib.Env.Env
import ib.data.{Persons, Person}
import ib.handler.CalPi
import spray.http.MediaTypes
import spray.http.MediaTypes._
import spray.httpx.marshalling._
import spray.routing.{HttpServiceActor, HttpService}
import ib.data.formatter.JsonFormatter._
import ib.Env
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by qili on 23/08/2015.
 */
class IBServiceActor(val env: Env) extends IBService with ActorLogging {

  def receive = runRoute(routeHandler)
}

trait IBService extends HttpServiceActor {
  implicit val env: Env.Value
  val routeHandler = path("") {
    get {
      respondWithMediaType(`text/html`) {
        complete {
          <html>
            <content>
              <h1>
                welcome to the web service!
              </h1>
            </content>
          </html>
        }
      }
    }
  } ~ path("pi") {
    get {
      respondWithMediaType(MediaTypes.`application/json`) {
        complete {
          val pi = new CalPi().process
          marshal(pi)
        }
      }
    }
  } ~ path("person") {
    get {
      respondWithMediaType(MediaTypes.`application/json`) {
        complete {
          val p = Persons.all.collect
          val res = "{" + p.map(_.toJsonString).mkString(",") + "}"
          marshal(res)
        }
      }
    }
  } ~ path("person" / Segment) { name => {
    import spray.json._
    complete(Persons.all.filter(_.name == name).collect.toJson.prettyPrint)
  } ~ path("countdown") {
    get {
      request => {
        println("Start counting down...")
        context.actorOf(Props(classOf[CountDownStream], request.responder, 10))
      }
    }
  } ~ path("countdown" / Segment) { secs => {
    get {
      request => context.actorOf(Props(classOf[CountDownStream], request.responder, secs))
    }
  }
  }
  }

}
