package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureSpec extends AnyFlatSpec with Matchers {
  "A Cure" should "have a default progress of 0.0 and baseSpeed of 0.1" in {
    val cure = Cure()
    cure.progress shouldEqual 0.0
    cure.baseSpeed shouldEqual 0.1
  }

  it should "allow setting custom progress and baseSpeed values" in {
    val cure = Cure(progress = 0.5, baseSpeed = 0.2)
    cure.progress shouldEqual 0.5
    cure.baseSpeed shouldEqual 0.2
  }
}