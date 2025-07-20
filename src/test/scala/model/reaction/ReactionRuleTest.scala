package model.reaction

import model.core.SimulationState
import model.cure.Cure
import model.infection.InfectionAndDeathPopulation.Infection.Death.StandardDeath
import model.infection.InfectionAndDeathPopulation.Infection.StandardInfection
import model.plague.Disease
import model.reaction.ReactionAction.CloseEdges
import model.reaction.Reactions.StandardReactions
import model.time.BasicYear
import model.time.TimeTypes.*
import model.world.{EdgeType, Node, Static, World}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReactionRuleTest extends AnyFlatSpec with Matchers:
  def testSimulationState: SimulationState =
    val defaultNode  = Node.Builder(100, 0, 0).build()
    val initialNodes = Map("A" -> defaultNode, "B" -> defaultNode)
    val initialWorld = World(initialNodes, Set.empty, Map(Static -> 1.0))
    SimulationState(
      BasicYear(Day(0), Year(2023)),
      null,
      null,
      initialWorld,
      null,
      null,
      null
    )

  "Reaction" should "trigger when the condition is satisfied" in:
    val cond     = InfectedCondition(threshold = 0.0)
    val reaction =
      ReactionRule(cond, nodeId => CloseEdges(EdgeType.Land, nodeId))
    val state = testSimulationState
    reaction.shouldTrigger(state, "A") shouldBe true

  it should "not trigger when the condition is not satisfied" in:
    val cond     = InfectedCondition(threshold = 0.5)
    val reaction =
      ReactionRule(cond, nodeId => CloseEdges(EdgeType.Land, nodeId))
    val state = testSimulationState
    reaction.shouldTrigger(state, "A") shouldBe false
