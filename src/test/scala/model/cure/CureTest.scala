package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureTest extends AnyFlatSpec with Matchers:
  "Cure" should "calculate effectiveSpeed correctly with no modifiers" in:
    val cure = Cure(progress = 0.0, baseSpeed = 0.05)
    cure.effectiveSpeed shouldEqual (0.05 +- 0.0001)

  it should "calculate effectiveSpeed with a multiplier modifier" in:
    val id =
      ModifierId(ModifierSource.Node(NodeId("n1")), ModifierKind.Multiplier)
    val mod  = CureModifier.Multiplier(id, 2.0)
    val cure = Cure(baseSpeed = 0.05, modifiers = CureModifiers.empty.add(mod))
    cure.effectiveSpeed shouldEqual (0.10 +- 0.0001)

  it should "calculate effectiveSpeed with an additive modifier" in:
    val id = ModifierId(
      ModifierSource.Mutation(MutationId("m1")),
      ModifierKind.Additive
    )
    val mod  = CureModifier.Additive(id, 0.03)
    val cure = Cure(baseSpeed = 0.05, modifiers = CureModifiers.empty.add(mod))
    cure.effectiveSpeed shouldEqual (0.08 +- 0.0001)

  it should "advance progress correctly and not exceed 1.0" in:
    val cure     = Cure(progress = 0.99, baseSpeed = 0.02)
    val advanced = cure.advance()
    advanced.progress shouldEqual (1.0 +- 0.0001)

  it should "allow for multiple modifiers" in:
    val id1 =
      ModifierId(ModifierSource.Node(NodeId("n1")), ModifierKind.Multiplier)
    val mod1 = CureModifier.Multiplier(id1, 2.0)
    val id2  = ModifierId(
      ModifierSource.Mutation(MutationId("m1")),
      ModifierKind.Additive
    )
    val mod2 = CureModifier.Additive(id2, 0.03)

    val cure = Cure(
      baseSpeed = 0.05,
      modifiers = CureModifiers.empty.add(mod1).add(mod2)
    )
    cure.effectiveSpeed shouldEqual (0.10 + 0.03) +- 0.0001

  it should "add and remove modifiers in CureModifiers" in:
    val id1 =
      ModifierId(ModifierSource.Node(NodeId("n1")), ModifierKind.Multiplier)
    val mod1 = CureModifier.Multiplier(id1, 2.0)
    val id2  = ModifierId(
      ModifierSource.Mutation(MutationId("m1")),
      ModifierKind.Additive
    )
    val mod2 = CureModifier.Additive(id2, 0.03)

    val modifiers = CureModifiers.empty.add(mod1).add(mod2)
    val removed   = modifiers.removeById(id1)
    removed.modifiers shouldNot contain key id1
    removed.modifiers should contain key id2

  "if a OneTimeModifier is added" should "apply its effect and not persist" in:
    val id = ModifierId(
      ModifierSource.Mutation(MutationId("m1")),
      ModifierKind.ProgressModifier
    )
    val mod = CureModifier.ProgressModifier(id, 0.2)
    val cure = Cure(progress = 0.5).addModifier(mod)
    cure.progress shouldEqual (0.7 +- 0.0001)
    cure.modifiers.modifiers should contain key id
    val newCure = cure.advance()
    newCure.progress shouldEqual (0.7 + newCure.baseSpeed +- 0.0001)

  "if the same modifier is added twice" should "only be considered once" in {
    val id = ModifierId(
      ModifierSource.Mutation(MutationId("m1")),
      ModifierKind.Additive
    )
    val mod = CureModifier.Additive(id, 0.05)
    val modifiers = CureModifiers.empty.add(mod).add(mod)
    modifiers.modifiers.size shouldEqual 1
    modifiers.modifiers should contain key id
    modifiers.modifiers(id) shouldEqual mod
  }
