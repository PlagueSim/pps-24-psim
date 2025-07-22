package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.events.*
import model.core.SimulationState
import model.cure.CureModifier.{Additive, ProgressModifier}
import model.events.cure.{
  AdvanceCureEvent,
  LinearInfectedThresholdEvent,
  ProgressSubtractExampleEvent
}
import model.world.{MovementStrategy, Static, World}
import model.time.TimeTypes.{Day, Year}
import model.time.*

class CureEventTest extends AnyFlatSpec with Matchers:
  def simulationState(
      cure: Cure = Cure(0.0, 0.1),
      nodes: Map[String, model.world.Node] = Map.empty
  ): SimulationState = SimulationState(
    time = BasicYear(Day(0), Year(2023)),
    disease = null,
    cure = cure,
    world = World(nodes, Set.empty, Map(Static -> 1.0)),
    infectionLogic = null,
    deathLogic = null,
    reactions = null
  )

  "AdvanceCureEvent" should "advance cure progress by effective speed" in {
    val state = simulationState(cure = Cure(progress = 0.3, baseSpeed = 0.1))
    val event = AdvanceCureEvent()
    val (newState, _) = event.execute().run(state).value

    newState.cure.progress shouldEqual 0.4
  }

  "LinearInfectedThresholdEvent" should "add modifiers for infected nodes" in {
    val nodes = Map(
      "A" -> model.world.Node.Builder(100, 60).build(), // 60% infected
      "B" -> model.world.Node.Builder(100, 30).build()  // 30% infected
    )
    val state     = simulationState(nodes = nodes)
    val event     = LinearInfectedThresholdEvent(threshold = 0.5)
    val (_, cure) = event.execute().run(state).value

    val modId =
      ModifierId(ModifierSource.Node(NodeId("A")), ModifierKind.Additive)
    cure.modifiers.modifiers should contain key modId
  }

  it should "not add duplicate modifiers" in {
    val nodes = Map("A" -> model.world.Node.Builder(100, 60).build())
    val state = simulationState(
      nodes = nodes,
      cure = Cure().addModifier(
        Additive(
          ModifierId(ModifierSource.Node(NodeId("A")), ModifierKind.Additive),
          0.01
        )
      )
    )
    val event     = LinearInfectedThresholdEvent(0.5)
    val (_, cure) = event.execute().run(state).value

    cure.modifiers.modifiers.count(
      _._1.source == ModifierSource.Node(NodeId("A"))
    ) shouldEqual 1
  }

  "ProgressSubtractExampleEvent" should "apply progress reduction immediately" in {
    val state = simulationState(cure = Cure(progress = 0.5, baseSpeed = 0.1))
    val event = ProgressSubtractExampleEvent(-0.2)
    val (_, cure) = event.execute().run(state).value

    cure.progress shouldEqual 0.3
  }

  it should "use global modifier source" in {
    val state     = simulationState()
    val event     = ProgressSubtractExampleEvent(-0.1)
    val (_, cure) = event.execute().run(state).value

    val mod = cure.modifiers.modifiers.values.collectFirst {
      case m: ProgressModifier => m
    }

    mod shouldBe defined
    mod.get.id.source shouldBe ModifierSource.Global
  }
