package ib

import ib.data.{Persons, Person}
import org.junit.Test

/**
 * Created by qili on 02/09/2015.
 */
class PersonTest {

  @Test
  def saveAndQuery = {
    implicit val env = Env.DEV
    val persons = Seq(Person("Tom", "Oxford", 18), Person("Kate", "Cambridge", 19))
    Persons.save(persons)
    val all = Persons.all
    all foreach println
  }

}
