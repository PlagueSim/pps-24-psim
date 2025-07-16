package model.cure

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.events.*
import model.core.SimulationState
import model.cure.Cure
import model.plague.Disease
import model.world.{MovementStrategy, Static, World}
import model.time.TimeTypes.{Day, Year}
import model.time.*

class CureEventTest extends AnyFlatSpec with Matchers:
  def basicSimulationState = SimulationState(
    BasicYear(Day(0), Year(2023)),
    Disease("TestDisease", Set.empty, 1),
    Cure(),
    World(Map.empty, Set.empty, Map(Static -> 1.0))
  )

  "BasicCureEvent" should "advance the cure progress when executed" in:
    val initialState = basicSimulationState
    val event = BasicCureEvent()

    val (newState, cure) = event.execute().run(initialState).value
    newState.cure.progress shouldEqual initialState.cure.progress + initialState.cure.baseSpeed

  it should "allow to be executed multiple times" in:
    val initialState = basicSimulationState
    val event = BasicCureEvent()

    val (firstState, _) = event.execute().run(initialState).value
    val (secondState, _) = event.execute().run(firstState).value

    secondState.cure.progress shouldEqual initialState.cure.progress + 2 * initialState.cure.baseSpeed

  "LinearInfectedThresholdEvent" should "add additive modifier only for nodes above threshold" in:
    val nodeA = model.world.Node.Builder(population = 100, infected = 60, cureEffectiveness = 0.0).build()
    val nodeB = model.world.Node.Builder(population = 100, infected = 30, cureEffectiveness = 0.0).build()
    val nodes = Map("A" -> nodeA, "B" -> nodeB)
    val state = basicSimulationState.copy(world = World(nodes, Set.empty, Map(Static -> 1.0)))
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
