package ib

import org.junit.Test

import scala.collection
import scala.collection.parallel.mutable

/**
  * Created by qili on 29/10/15.
  */
class OperatorTest {

  @Test
  def testOrder: Unit = {
    trait Orchestor {
      type Deletable = {def delete: Unit}
      val deletables = new collection.mutable.Stack[Deletable]()

      def :::(o: Deletable) = {
        deletables.push(o)
      }

      val toBeDeleted = this

      def delete = for (o <- deletables) {
        try {
          o.delete
        }
      }
    }
    case class Dummy(id: String) {
      def delete = println(s"Delete $id")
    }
    class ObjectOrchestor extends Orchestor {
      def run = {
        val o1 = Dummy("o1")
        val o2 = Dummy("o2")
        o1 ::: toBeDeleted
        o2 ::: toBeDeleted
        this.delete
      }
    }
    val o = new ObjectOrchestor
    o.run

  }

}
