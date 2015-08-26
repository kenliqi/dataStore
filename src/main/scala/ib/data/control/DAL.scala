package ib.data.control

import scala.reflect.ClassTag

/**
 * Created by qili on 25/08/2015.
 *
 *
 */
trait DAL {
  def keySpace: String

  def createSchema(e: Class[_]): Boolean

  def persist[T: ClassTag](e: T): T

  //TODO: need more thinking
  def query[T: ClassTag](q: String): Seq[T]
}
