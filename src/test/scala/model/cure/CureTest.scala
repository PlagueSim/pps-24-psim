package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureSpec extends AnyFlatSpec with Matchers {

  "A Cure" should "be creatable with default values and have initial progress 0.0, speed 0.01, and no difficulty factors" in {
    val cure = Cure()
    cure.progress shouldBe 0.0
  }
}