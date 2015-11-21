package ib.data

import ib.util.DateUtil
import spray.json.{JsString, JsValue, RootJsonFormat, DefaultJsonProtocol}

/**
  * Created by qili on 25/08/2015.
  *
  * TODO: May come up with a generic Json convertor for all entities
  *
  */
object JsonFormatter extends DefaultJsonProtocol {

  implicit val DateFormat = new RootJsonFormat[java.util.Date] {
    def read(json: JsValue): java.util.Date = DateUtil.SDF.parse(json.compactPrint)

    def write(date: java.util.Date) = JsString(DateUtil.SDF.format(date))
  }
  implicit val quoteFormatter = jsonFormat6(Quote)

  implicit val tsFormatter = jsonFormat2(TimeSeries)
}
