package ib.data.formatter

import ib.data._
import spray.json.DefaultJsonProtocol

/**
 * Created by qili on 25/08/2015.
 *
 * TODO: May come up with a generic Json convertor for all entities
 *
 */
object JsonFormatter extends DefaultJsonProtocol {
  implicit val piFormatter = jsonFormat1(Pi)
  implicit val personFormatter = jsonFormat3(Person)
}

