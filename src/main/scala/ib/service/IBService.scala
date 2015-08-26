package ib.service

import akka.actor.Actor
import ib.data.{Persons, Person}
import ib.handler.CalPi
import spray.http.MediaTypes
import spray.http.MediaTypes._
import spray.httpx.marshalling._
import spray.routing.HttpService
import ib.data.formatter.JsonFormatter._
import ib.Env

/**
 * Created by qili on 23/08/2015.
 */
class IBServiceActor extends IBService with Actor {
  def actorRefFactory = context

  def receive = runRoute(routeHandler)
}

trait IBService extends HttpService {
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

          implicit val env = Env.DEV
          val p = Persons.all.collect
          val res = "{" + p.map(_.toJsonString).mkString(",") + "}"
          marshal(res)
        }
      }
    }
  }
}
