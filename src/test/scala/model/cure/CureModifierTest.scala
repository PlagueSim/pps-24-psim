package model.cure

import model.cure.CureModifier.{Additive, Multiplier, ProgressModifier}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureModifierTest extends AnyFlatSpec with Matchers:
  val nodeId: ModifierId =
    ModifierId(ModifierSource.Node(NodeId("testNode")), ModifierKind.Multiplier)
  val mutationId: ModifierId = ModifierId(
    ModifierSource.Mutation(MutationId("testMutation")),
    ModifierKind.Additive
  )
  val globalId: ModifierId =
    ModifierId(ModifierSource.Global, ModifierKind.ProgressModifier)

  "A CureModifier" should "correctly implement modifier types" in:
    val multiplier  = CureModifier.multiplier(nodeId, 2.0).get
    val additive    = CureModifier.additive(mutationId, 0.05).get
    val progressMod = CureModifier.progressModifier(globalId, -0.1).get

    multiplier shouldBe a[PersistentModifier]
    additive shouldBe a[PersistentModifier]
    progressMod shouldBe a[OneTimeModifier]

  it should "apply multiplier correctly" in:
    CureModifier.multiplier(nodeId, 2.0).get(0.1) shouldEqual 0.2

  it should "apply additive correctly and clamp values" in:
    CureModifier.additive(mutationId, 0.05).get(0.1) shouldEqual 0.15 +- 0.00001
    CureModifier.additive(mutationId, 1.0).get(0.5) shouldEqual 1.0
    CureModifier.additive(mutationId, -0.6).get(0.5) shouldEqual 0.0

  it should "apply progress modifier correctly and clamp values" in:
    CureModifier.progressModifier(globalId, 0.1).get(0.3) shouldEqual 0.4 +- 0.00001
    CureModifier.progressModifier(globalId, -0.2).get(0.3) shouldEqual 0.1 +- 0.00001
    CureModifier.progressModifier(globalId, 0.8).get(0.5) shouldEqual 1.0 +- 0.00001
    CureModifier.progressModifier(globalId, -1.0).get(0.5) shouldEqual 0.0 +- 0.00001

  "ModifierId" should "implement proper equality" in:
    val id1 =
      ModifierId(ModifierSource.Node(NodeId("A")), ModifierKind.Additive)
    val id2 =
      ModifierId(ModifierSource.Node(NodeId("A")), ModifierKind.Additive)
    val id3 =
      ModifierId(ModifierSource.Node(NodeId("B")), ModifierKind.Additive)

    id1 shouldEqual id2
    id1 should not equal id3
