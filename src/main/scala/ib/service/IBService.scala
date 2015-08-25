package ib.service

import akka.actor.Actor
import ib.handler.CalPi
import spray.http.MediaTypes
import spray.http.MediaTypes._
import spray.httpx.marshalling._
import spray.routing.HttpService

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
  }
}
