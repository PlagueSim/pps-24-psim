package model.events.cure

import model.core.SimulationState
import model.cure.{
  Cure,
  CureModifier,
  CureModifiers,
  ModifierId,
  ModifierKind,
  ModifierSource,
  NodeId
}
import model.plague.{Disease, Symptoms, Trait}
import model.world.{Node, RandomNeighbor, Static, World}
import model.time.TimeTypes.{Day, Year}
import model.time.BasicYear
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.OptionValues

class GlobalCureResearchEventTest extends AnyFlatSpec with Matchers:

  private def state(
      traits: Set[Trait] = Set.empty,
      nodes: Map[String, Node] = Map(
        "A" -> Node
          .withPopulation(100)
          .withInfected(60)
          .build(), // 60% infected
        "B" -> Node
          .withPopulation(200)
          .withInfected(160)
          .build() // 80% infected
      ),
      cure: Cure = Cure()
  ): SimulationState =
    SimulationState(
      time = BasicYear(Day(0), Year(2023)),
      disease = Disease("TestDisease", traits = traits, dnaPoints = 0),
      cure = cure,
      world = World(
        nodes = nodes,
        edges = Map.empty,
        movements = Map(Static -> 1)
      ),
      infectionLogic = null,
      deathLogic = null,
      reactions = null
    )

  private def lowSeverityTraits: Set[Trait] = Set(
    Symptoms.paranoia,
    Symptoms.abscesses
  ) // severity < 20

  private def highSeverityTraits: Set[Trait] =
    Set(
      Symptoms.paranoia,
      Symptoms.abscesses,
      Symptoms.pulmonaryEdema,
      Symptoms.diarrhea,
      Symptoms.skinLesions,
      Symptoms.rash
    ) // severity >= 20

  "GlobalCureResearchEvent" should "not modify cure when disease severity is below threshold" in:
    val s = state(traits = lowSeverityTraits)

    val result = GlobalCureResearchEvent.modifyFunction(s)
    result shouldBe s.cure

  it should "add modifiers and increase effective speed" in:
    val s = state(traits = highSeverityTraits, cure = Cure(baseSpeed = 0.1))
    val severity = s.disease.severity

    val result = GlobalCureResearchEvent.modifyFunction(s)

    // Check presence of modifiers
    val modAId =
      ModifierId(ModifierSource.Node(NodeId("A")), ModifierKind.Additive)
    val modBId =
      ModifierId(ModifierSource.Node(NodeId("B")), ModifierKind.Additive)

    result.modifiers.modifiers should contain key modAId
    result.modifiers.modifiers should contain key modBId

    // Check effect on calculations
    result.effectiveSpeed shouldBe (0.1 + (60.0 / 300) + (160.0 / 300)) +- 0.001

  it should "correctly calculate contributions from nodes" in:
    val s = state(traits = highSeverityTraits, cure = Cure(baseSpeed = 0.0))
    val result = GlobalCureResearchEvent.modifyFunction(s)

    // Check cumulative effect
    // 60k/300k + 160k/300k = 0.2 + 0.5333 = 0.7333
    result.effectiveSpeed shouldBe (0.7333 +- 0.001)

  it should "only add modifiers for nodes with infected population above threshold" in:
    val s = state(
      traits = highSeverityTraits,
      nodes = Map(
        "A" -> Node.withPopulation(100).withInfected(60).build(),
        "B" -> Node.withPopulation(200).withInfected(160).build(),
        "C" -> Node.withPopulation(100).withInfected(0).build() // Zero infected
      )
    )
    val result = GlobalCureResearchEvent.modifyFunction(s)
    result.modifiers.modifiers should contain key ModifierId(
      ModifierSource.Node(NodeId("A")),
      ModifierKind.Additive
    )
    result.modifiers.modifiers should contain key ModifierId(
      ModifierSource.Node(NodeId("B")),
      ModifierKind.Additive
    )
    result.modifiers.modifiers shouldNot contain key ModifierId(
      ModifierSource.Node(NodeId("C")),
      ModifierKind.Additive
    )

  it should "not add modifiers for nodes with zero infected" in:
    val s = state(
      traits = highSeverityTraits,
      nodes = Map(
        "C" -> Node.withPopulation(100).withInfected(0).build() // Zero infected
      )
    )
    val result = GlobalCureResearchEvent.modifyFunction(s)
    result.modifiers.modifiers shouldBe empty

  it should "handle nodes with zero total population" in:
    val s = state(
      traits = highSeverityTraits,
      nodes = Map(
        "D" -> Node
          .withPopulation(0)
          .withInfected(0)
          .build() // Zero population
      )
    )
    val result = GlobalCureResearchEvent.modifyFunction(s)
    result.modifiers.modifiers shouldBe empty

  it should "remove a modifier if infected ratio decreases below threshold" in:
    val existingModId =
      ModifierId(ModifierSource.Node(NodeId("A")), ModifierKind.Additive)

    val s = state(
      traits = highSeverityTraits,
      nodes = Map("A" -> Node.withPopulation(100).withInfected(10).build()),
      cure = Cure()
        .addModifier(CureModifier.additive(existingModId, 0.5).get)
    )

    val result = GlobalCureResearchEvent.modifyFunction(s)
    result.modifiers.modifiers shouldBe empty

  it should "keep existing modifiers from other sources" in:
    val globalModId = ModifierId(ModifierSource.Global, ModifierKind.Additive)
    val globalModifier = CureModifier.additive(globalModId, 0.1).get

    val s = state(
      traits = highSeverityTraits,
      nodes = Map("A" -> Node.withPopulation(100).withInfected(70).build()),
      cure = Cure(baseSpeed = 0.0).addModifier(globalModifier)
    )

    val result = GlobalCureResearchEvent.modifyFunction(s)

    // Should have 2 modifiers: global + node A
    result.modifiers.modifiers should have size 2

    // Check combined effect
    // (baseSpeed + additive1 + additive2) = (0.0 + 0.1 + 0.7) = 0.8
    result.effectiveSpeed shouldBe 0.8 +- 0.001

  it should "calculate contribution relative to total population" in:
    val s = state(
      traits = highSeverityTraits,
      nodes = Map(
        "A" -> Node.withPopulation(100).withInfected(60).build(),
        "B" -> Node.withPopulation(200).withInfected(10).build()
      ),
      cure = Cure(baseSpeed = 0.0)
    )

    val result = GlobalCureResearchEvent.modifyFunction(s)

    // Total population = 300, total infected = 70
    // Only A contributes: 60/300 = 0.2
    result.effectiveSpeed shouldBe (0.2 +- 0.001)
