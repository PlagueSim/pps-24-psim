package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks._

class CureTest extends AnyFlatSpec with Matchers:
  def defaultCure(progress: Double = 0.0, baseSpeed: Double = 0.1, modifiers: CureModifiers = CureModifiers.empty) =
    Cure(progress = progress, baseSpeed = baseSpeed, modifiers = modifiers)

  "A Cure" should "have correct default and custom values" in {
    val cases = Table(
      ("cure", "expectedProgress", "expectedBaseSpeed", "expectedModifiers"),
      (defaultCure(), 0.0, 0.1, CureModifiers.empty),
      (defaultCure(progress = 0.5), 0.5, 0.1, CureModifiers.empty),
      (defaultCure(baseSpeed = 0.2), 0.0, 0.2, CureModifiers.empty),
      (defaultCure(modifiers = CureModifiers.empty.add(CureModifier.Additive(0.1))), 0.0, 0.1, CureModifiers.empty.add(CureModifier.Additive(0.1)))
    )
    forAll(cases) { (cure, expectedProgress, expectedBaseSpeed, expectedModifiers) =>
      cure.progress shouldEqual expectedProgress
      cure.baseSpeed shouldEqual expectedBaseSpeed
      cure.modifiers shouldEqual expectedModifiers
    }
  }

  it should "allow adding and removing modifiers" in {
    val modifier1 = CureModifier.Multiplier(2.0)
    val modifier2 = CureModifier.Additive(0.5)
    val modifier3 = CureModifier.MinThreshold(0.3)
    val mods = CureModifiers.empty.add(modifier1).add(modifier2).add(modifier3)
    val cure = defaultCure(modifiers = mods)
    cure.modifiers.factors should contain allElementsOf List(modifier1, modifier2, modifier3)
    cure.modifiers.remove(_ == modifier1).factors should contain theSameElementsAs List(modifier3, modifier2)
  }

  it should "calculate effective speed with modifiers" in {
    val cases = Table(
      ("modifiers", "expectedSpeed"),
      (CureModifiers.empty.add(CureModifier.Multiplier(2.0)), 0.2),
      (CureModifiers.empty.add(CureModifier.Multiplier(2.0)).add(CureModifier.Additive(0.1)), 0.3)
    )
    forAll(cases) { (modifiers, expectedSpeed) =>
      val cure = defaultCure(baseSpeed = 0.1, modifiers = modifiers)
      cure.effectiveSpeed shouldBe (expectedSpeed +- 1e-9)
    }
  }
