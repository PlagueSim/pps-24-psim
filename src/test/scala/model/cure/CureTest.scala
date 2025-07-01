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

  it should "allow adding a modifier" in {
    val modifier = new CureModifier {}
    val cure     = Cure().copy(modifiers = CureModifiers.empty.add(modifier))
    cure.modifiers.factors should contain(modifier)
  }

  it should "allow adding multiple modifiers" in {
    case class TestModifier(name: String)  extends CureModifier
    case class AnotherModifier(value: Int) extends CureModifier
    val modifier1 = TestModifier("Test")
    val modifier2 = AnotherModifier(42)
    val modifier3 = TestModifier("AnotherTest")
    val cure      = Cure().copy(modifiers =
      CureModifiers.empty.add(modifier1).add(modifier2).add(modifier3)
    )
    cure.modifiers
      .remove(_ == modifier1)
      .factors should contain theSameElementsAs List(modifier3, modifier2)
  }
}
