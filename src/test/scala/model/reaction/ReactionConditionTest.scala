package model.reaction

import model.core.SimulationState
import model.cure.Cure
import model.plague.Disease
import model.time.BasicYear
import model.time.TimeTypes.*
import model.world.World
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReactionConditionTest extends AnyFlatSpec with Matchers:
  def testSimulationState: SimulationState =
    val defaultNode  = model.world.Node.Builder(100, 0, 0).build()
    val initialNodes =
      Map("A" -> defaultNode, "B" -> defaultNode, "C" -> defaultNode)
    val initialWorld =
      World(initialNodes, Set.empty, Map(model.world.Static -> 1.0))
    SimulationState(
      BasicYear(Day(0), Year(2023)),
      null,
      null,
      initialWorld,
      null,
      null,
      null
    )

  def simulationStateWithInfected(
      nodeId: String,
      infectedCount: Int
  ): SimulationState =
    val baseState   = testSimulationState
    val node        = baseState.world.nodes(nodeId)
    val updatedNode = model.world.Node
      .Builder(
        node.population,
        infectedCount,
        node.died
      )
      .build()
    val updatedWorld = World(
      baseState.world.nodes.updated(nodeId, updatedNode),
      baseState.world.edges,
      baseState.world.movements
    )
    SimulationState(
      baseState.time,
      baseState.disease,
      baseState.cure,
      updatedWorld,
      baseState.infectionLogic,
      baseState.deathLogic,
      baseState.reactions
    )

  "InfectedCondition" should "not be satisfied if infected is below threshold" in:
    val state = simulationStateWithInfected("A", 10)
    val cond  = InfectedCondition(threshold = 0.2)
    cond.isSatisfied(state, "A") shouldBe false

  it should "be satisfied if infected meets or exceeds threshold" in:
    val state = simulationStateWithInfected("B", 20)
    val cond  = InfectedCondition(threshold = 0.2)
    cond.isSatisfied(state, "B") shouldBe true

  it should "return false for untouched node" in:
    val state = simulationStateWithInfected("A", 50)
    val cond  = InfectedCondition(threshold = 0.1)
    cond.isSatisfied(state, "C") shouldBe false

  it should "return false for non-existent node" in:
    val state = testSimulationState
    val cond  = InfectedCondition(threshold = 0.1)
    cond.isSatisfied(state, "Z") shouldBe false

  it should "return false for node with zero population" in:
    val state = simulationStateWithInfected("A", 0)
    val zeroPopNode = model.world.Node.Builder(0, 0, 0).build()
    val updatedWorld = state.world.modifyNodes(state.world.nodes.updated("A", zeroPopNode))
    val updatedState = SimulationState(
      state.time,
      state.disease,
      state.cure,
      updatedWorld,
      state.infectionLogic,
      state.deathLogic,
      state.reactions
    )
    val cond  = InfectedCondition(threshold = 0.1)
    cond.isSatisfied(updatedState, "A") shouldBe false
