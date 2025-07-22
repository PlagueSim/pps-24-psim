package model.cure

import model.cure.CureModifier.{Additive, Multiplier, ProgressModifier}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureModifiersTest extends AnyFlatSpec with Matchers:
  val nodeId = ModifierId(ModifierSource.Node(NodeId("n1")), ModifierKind.Multiplier)
  val mutationId = ModifierId(ModifierSource.Mutation(MutationId("m1")), ModifierKind.Additive)
  val globalId = ModifierId(ModifierSource.Global, ModifierKind.ProgressModifier)

  val multiplier = Multiplier(nodeId, 2.0)
  val additive = Additive(mutationId, 0.03)
  val progressMod = ProgressModifier(globalId, -0.15)

  "CureModifiers" should "correctly add and remove modifiers" in:
    val modifiers = CureModifiers.empty
      .add(multiplier)
      .add(additive)
      .add(progressMod)

    modifiers.modifiers should have size 3
    modifiers.modifiers should contain key nodeId
    modifiers.modifiers should contain key mutationId
    modifiers.modifiers should contain key globalId

    val removed = modifiers.removeById(nodeId)
    removed.modifiers should have size 2
    removed.modifiers should not contain key(nodeId)

  it should "filter modifiers by source" in:
    val modifiers = CureModifiers.empty
      .add(multiplier)
      .add(additive)
      .add(progressMod)

    val nodeSource = ModifierSource.Node(NodeId("n1"))
    val filtered = modifiers.removeBySource(nodeSource)

    filtered.modifiers should have size 2
    filtered.modifiers should not contain key(nodeId)

  it should "only include persistent modifiers in factors" in:
    val modifiers = CureModifiers.empty
      .add(multiplier)
      .add(additive)
      .add(progressMod)

    // Verifica la dimensione
    modifiers.factors should have size 2

    // Verifica il comportamento delle funzioni
    val testInput = 0.5
    val expectedResults = Set(
      multiplier.apply(testInput),  // 0.5 * 2.0 = 1.0
      additive.apply(testInput)     // 0.5 + 0.03 = 0.53
    )

    val actualResults = modifiers.factors.map(_(testInput)).toSet
    actualResults should contain allElementsOf expectedResults
