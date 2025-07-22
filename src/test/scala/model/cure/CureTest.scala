package model.cure

import model.cure.CureModifier.{Additive, Multiplier, ProgressModifier}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CureTest extends AnyFlatSpec with Matchers:

  val nodeId = ModifierId(ModifierSource.Node(NodeId("n1")), ModifierKind.Multiplier)
  val mutationId = ModifierId(ModifierSource.Mutation(MutationId("m1")), ModifierKind.Additive)
  val globalId = ModifierId(ModifierSource.Global, ModifierKind.ProgressModifier)

  val multiplier = Multiplier(nodeId, 2.0)
  val additive = Additive(mutationId, 0.03)
  val progressMod = ProgressModifier(globalId, -0.15)

  "Cure" should "correctly advance progress" in {
    val cure = Cure(progress = 0.3, baseSpeed = 0.1)
    val advanced = cure.advance()

    advanced.progress shouldEqual 0.4
  }

  it should "not exceed 1.0 when advancing" in {
    val cure = Cure(progress = 0.95, baseSpeed = 0.1)
    cure.advance().progress shouldEqual 1.0
  }

  it should "add persistent modifiers correctly" in {
    val cure = Cure(baseSpeed = 0.1)
      .addModifier(multiplier)
      .addModifier(additive)

    cure.effectiveSpeed shouldEqual (0.1 * 2.0) + 0.03
    cure.modifiers.modifiers should contain key nodeId
    cure.modifiers.modifiers should contain key mutationId
  }

  it should "apply one-time modifiers immediately" in {
    val cure = Cure(progress = 0.5)
      .addModifier(progressMod)

    cure.progress shouldEqual 0.35
    cure.modifiers.modifiers should contain key globalId
  }

  it should "clamp progress after one-time modification" in {
    Cure(progress = 0.1).addModifier(ProgressModifier(globalId, -0.2)).progress shouldEqual 0.0
    Cure(progress = 0.9).addModifier(ProgressModifier(globalId, 0.2)).progress shouldEqual 1.0
  }

  it should "remove modifiers correctly" in {
    val cure = Cure()
      .addModifier(multiplier)
      .addModifier(additive)
      .removeModifierById(nodeId)

    cure.modifiers.modifiers should not contain key(nodeId)
    cure.modifiers.modifiers should contain key (mutationId)
  }
