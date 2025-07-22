package model.cure

import model.cure.CureModifier.{Additive, Multiplier, ProgressModifier}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureModifierTest extends AnyFlatSpec with Matchers:
  val nodeId =
    ModifierId(ModifierSource.Node(NodeId("testNode")), ModifierKind.Multiplier)
  val mutationId = ModifierId(
    ModifierSource.Mutation(MutationId("testMutation")),
    ModifierKind.Additive
  )
  val globalId =
    ModifierId(ModifierSource.Global, ModifierKind.ProgressModifier)

  "A CureModifier" should "correctly implement modifier types" in:
    val multiplier  = Multiplier(nodeId, 2.0)
    val additive    = Additive(mutationId, 0.05)
    val progressMod = ProgressModifier(globalId, -0.1)

    multiplier shouldBe a[PersistentModifier]
    additive shouldBe a[PersistentModifier]
    progressMod shouldBe a[OneTimeModifier]

  it should "apply multiplier correctly" in:
    Multiplier(nodeId, 2.0)(0.1) shouldEqual 0.2

  it should "apply additive correctly and clamp values" in:
    Additive(mutationId, 0.05)(0.1) shouldEqual 0.15 +- 0.00001
    Additive(mutationId, 1.0)(0.5) shouldEqual 1.0
    Additive(mutationId, -0.6)(0.5) shouldEqual 0.0

  it should "apply progress modifier correctly and clamp values" in:
    ProgressModifier(globalId, 0.1)(0.3) shouldEqual 0.4 +- 0.00001
    ProgressModifier(globalId, -0.2)(0.3) shouldEqual 0.1 +- 0.00001
    ProgressModifier(globalId, 0.8)(0.5) shouldEqual 1.0 +- 0.00001
    ProgressModifier(globalId, -1.0)(0.5) shouldEqual 0.0 +- 0.00001

  "ModifierId" should "implement proper equality" in:
    val id1 =
      ModifierId(ModifierSource.Node(NodeId("A")), ModifierKind.Additive)
    val id2 =
      ModifierId(ModifierSource.Node(NodeId("A")), ModifierKind.Additive)
    val id3 =
      ModifierId(ModifierSource.Node(NodeId("B")), ModifierKind.Additive)

    id1 shouldEqual id2
    id1 should not equal id3
