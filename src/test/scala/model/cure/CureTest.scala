package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureTest extends AnyFlatSpec with Matchers:
  def defaultCure(
      progress: Double = 0.0,
      baseSpeed: Double = 0.1,
      modifiers: CureModifiers = CureModifiers.empty
  ): Cure =
    Cure(progress = progress, baseSpeed = baseSpeed, modifiers = modifiers)

  var additiveMod: CureModifier.Additive         = CureModifier.Additive(1.0)
  var multiplierMod: CureModifier.Multiplier     = CureModifier.Multiplier(2.0)
  var minThresholdMod: CureModifier.MinThreshold =
    CureModifier.MinThreshold(0.5)

  "A Cure" should "have correct default value" in:
    val cure = defaultCure()
    cure.progress shouldEqual 0.0

  it should "have correct custom progress value" in:
    val cure = defaultCure(progress = 0.5)
    cure.progress shouldEqual 0.5

  it should "have correct custom baseSpeed value" in:
    val cure = defaultCure(baseSpeed = 0.2)
    cure.baseSpeed shouldEqual 0.2

  it should "have empty modifiers by default" in:
    val cure = defaultCure()
    cure.modifiers shouldEqual CureModifiers.empty

  it should "be able to add modifiers" in:
    val cure = defaultCure().copy(modifiers = CureModifiers.empty.add(additiveMod))
    cure.modifiers.factors should contain(additiveMod)

  it should "be able to remove modifiers" in:
    val cure = defaultCure().copy(modifiers = CureModifiers.empty.add(additiveMod).add(multiplierMod))
    val updatedCure = cure.copy(modifiers = cure.modifiers.remove(_.isInstanceOf[CureModifier.Additive]))
    updatedCure.modifiers.factors should contain theSameElementsAs List(multiplierMod)
