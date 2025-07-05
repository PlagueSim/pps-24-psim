package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureModifierTest extends AnyFlatSpec with Matchers:
  "A Multiplier CureModifier" should "multiply the base speed" in:
    val modifier = CureModifier.Multiplier(2.0)
    modifier(0.5) shouldEqual 1.0

  "An Additive CureModifier" should "add to the base speed" in:
    val modifier = CureModifier.Additive(0.3)
    modifier(0.5) shouldEqual 0.8

  "A MinThreshold CureModifier" should "enforce a minimum base speed" in:
    val modifier = CureModifier.MinThreshold(0.7)
    modifier(0.5) shouldEqual 0.7

  it should "allow base speed above the minimum" in:
    val modifier = CureModifier.MinThreshold(0.7)
    modifier(0.8) shouldEqual 0.8
