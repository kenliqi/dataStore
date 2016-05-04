package ib

import ib.common.Retry
import org.junit.Test

/**
  * Created by qili on 04/05/2016.
  */
class RetryTest extends Retry {

  @Test
  def testTry: Unit = {
    def f = println("Calling")
    retry(3)(f)
  }


}
