package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureModifierTest extends AnyFlatSpec with Matchers:
  "A Multiplier CureModifier" should "multiply the base speed" in:
    val id = ModifierId(ModifierSource.Node(NodeId("testNode")), ModifierKind.Multiplier)
    val modifier = CureModifier.Multiplier(id, 2.0)
    modifier(0.1) shouldEqual (0.2 +- 0.0001)

  "An Additive CureModifier" should "add to the base speed" in:
    val id = ModifierId(ModifierSource.Mutation(MutationId("testMutation")), ModifierKind.Additive)
    val modifier = CureModifier.Additive(id, 0.05)
    modifier(0.1) shouldEqual (0.15 +- 0.0001)
