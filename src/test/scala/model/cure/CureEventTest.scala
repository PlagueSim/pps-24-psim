package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.events.*
import model.core.SimulationState
import model.events.cure.{AdvanceCureEvent, LinearInfectedThresholdEvent, ProgressSubtractExampleEvent}
import model.world.{MovementStrategy, Static, World}
import model.time.TimeTypes.{Day, Year}
import model.time.*

class CureEventTest extends AnyFlatSpec with Matchers:
  def basicSimulationState = SimulationState(
    BasicYear(Day(0), Year(2023)),
    null,
    Cure(),
    World(Map.empty, Set.empty, Map(Static -> 1.0)),
    null,
    null,
    null
  )

  "BasicCureEvent" should "advance the cure progress when executed" in:
    val initialState = basicSimulationState
    val event = AdvanceCureEvent()

    val (newState, cure) = event.execute().run(initialState).value
    newState.cure.progress shouldEqual initialState.cure.progress + initialState.cure.baseSpeed

  it should "allow to be executed multiple times" in:
    val initialState = basicSimulationState
    val event = AdvanceCureEvent()

    val (firstState, _) = event.execute().run(initialState).value
    val (secondState, _) = event.execute().run(firstState).value

    secondState.cure.progress shouldEqual initialState.cure.progress + 2 * initialState.cure.baseSpeed

  "LinearInfectedThresholdEvent" should "add additive modifier only for nodes above threshold" in:
    val nodeA = model.world.Node.Builder(population = 100, infected = 60).build()
    val nodeB = model.world.Node.Builder(population = 100, infected = 30).build()
    val nodes = Map("A" -> nodeA, "B" -> nodeB)
    val state = basicSimulationState.replace(World(nodes, Set.empty, Map(Static -> 1.0)))
    val event = LinearInfectedThresholdEvent(threshold = 0.5)

    val (_, cure) = event.execute().run(state).value
    val modIdA = model.cure.ModifierId(
      model.cure.ModifierSource.Node(model.cure.NodeId("A")),
      model.cure.ModifierKind.Additive
    )
    val modIdB = model.cure.ModifierId(
      model.cure.ModifierSource.Node(model.cure.NodeId("B")),
      model.cure.ModifierKind.Additive
    )
    cure.modifiers.modifiers should contain key modIdA
    cure.modifiers.modifiers shouldNot contain key modIdB

  "ProgressSubtractExampleEvent" should "subtract progress immediately and store the modifier" in:
    val initialState = basicSimulationState.replace(Cure(progress = 0.5))
    val event = model.events.cure.ProgressSubtractExampleEvent(-0.2)
    val (newState, cure) = event.execute().run(initialState).value
    // Progress should be subtracted immediately
    cure.progress shouldEqual 0.3

  "Modifier" should "be added to the cure modifiers" in:
    val initialState = basicSimulationState.replace(Cure(progress = 0.5))
    val event = ProgressSubtractExampleEvent(-0.2)
    val (newState, cure) = event.execute().run(initialState).value
    val modId = model.cure.ModifierId(
      model.cure.ModifierSource.Global,
      model.cure.ModifierKind.ProgressModifier
    )
    cure.modifiers.modifiers should contain key modId
    cure.modifiers.modifiers(modId) shouldBe a[model.cure.CureModifier.ProgressModifier]

  it should "be executed only once" in:
    val initialState = basicSimulationState.replace(Cure(progress = 0.5))
    val event = ProgressSubtractExampleEvent(-0.2)
    val cureAdvanceEvent = AdvanceCureEvent()
    val (firstState, firstCure) = event.execute().run(initialState).value
    val (secondState, secondCure) = cureAdvanceEvent.execute().run(firstState).value

    secondCure.progress shouldEqual firstCure.progress + secondCure.baseSpeed