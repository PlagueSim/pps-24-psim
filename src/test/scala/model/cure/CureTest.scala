package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureTest extends AnyFlatSpec with Matchers {
  "A Cure" should "have a default progress of 0.0" in {
    val cure = Cure()
    cure.progress shouldEqual 0.0
  }

  it should "have a default baseSpeed of 0.1" in {
    val cure = Cure()
    cure.baseSpeed shouldEqual 0.1
  }

  it should "allow setting a custom progress" in {
    val cure = Cure(progress = 0.5)
    cure.progress shouldEqual 0.5
  }

  it should "allow setting a custom baseSpeed" in {
    val cure = Cure(baseSpeed = 0.2)
    cure.baseSpeed shouldEqual 0.2
  }

  it should "have empty modifiers by default" in {
    val cure = Cure()
    cure.modifiers shouldEqual CureModifiers.empty
  }
}
