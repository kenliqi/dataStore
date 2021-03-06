package ib.main

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import ib.Env
import ib.service.IBServiceActor
import spray.can.Http

import scala.concurrent.duration._
/**
 * Created by qili on 23/08/2015.
 */
object Boot extends App {
  implicit val system = ActorSystem("sprayOnCan")
  implicit val env = Env.DEV
  val service = system.actorOf(Props(classOf[IBServiceActor], env), "IBService")

  implicit val timeout = Timeout(5.seconds)

  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
