package ib.data

import _root_.annotation.{entity, index, partitionKey}

/**
 * Created by qili on 25/08/2015.
 */
@entity
case class Person(@partitionKey val name: String, @index val school: String, val age: Int) {

}
