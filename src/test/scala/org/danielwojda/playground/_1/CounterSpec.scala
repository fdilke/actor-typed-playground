package org.danielwojda.playground._1

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class CounterSpec extends AnyFlatSpec with Matchers {

  "Counter Actor" must "count commands" in {
    1 mustBe 1
  }
}
