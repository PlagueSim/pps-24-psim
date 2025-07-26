package model.events.reactionsEvents

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.core.SimulationState
import model.reaction._
import model.world.{World, Node, Edge, EdgeType, MovementStrategy, Static}
import model.time.TimeTypes.{Day, Year}
import model.time._

class UpdateActiveReactionsEventTest extends AnyFlatSpec with Matchers:
  def testWorld: World =
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(10).build()
    World(
      Map("A" -> nodeA, "B" -> nodeB),
      Map("A-B" -> Edge("A", "B", EdgeType.Land)),
      Map(Static -> 1.0)
    )

  trait ConditionFactory {
    def apply(result: Boolean): ReactionCondition = new ReactionCondition {
      override def isSatisfied(
          state: SimulationState,
          nodeId: String
      ): Boolean = result
    }
  }
  object ConditionFactory extends ConditionFactory

  def ruleWithCondition(result: Boolean) =
    ReactionRule(
      ConditionFactory(result),
      nodeId => ReactionAction.CloseEdges(EdgeType.Land, nodeId)
    )

  def simulationState(
      time: Time = BasicYear(Day(0), Year(2023)),
      disease: model.plague.Disease = null,
      cure: model.cure.Cure = null,
      world: World = testWorld,
      infectionLogic: model.infection.PopulationStrategy =
        null,
      deathLogic: model.infection.PopulationStrategy =
        null,
      reactions: Reactions = Reactions()
  ): SimulationState =
    SimulationState(
      time,
      disease,
      cure,
      world,
      infectionLogic,
      deathLogic,
      reactions
    )

  "UpdateActiveReactionsEvent" should "remove expired reactions and activate new ones" in:
    val rule      = ruleWithCondition(true).copy(duration = Some(1))
    val expired   = ActiveReaction(rule, "A", BasicYear(Day(0), Year(2023)))
    val reactions =
      Reactions(rules = List(rule), activeReactions = Set(expired))
    val state = simulationState(
      time = BasicYear(Day(2), Year(2023)),
      reactions = reactions
    )
    val event   = new UpdateActiveReactionsEvent
    val updated = event.modifyFunction(state)
    // Expired should be removed, new should be added for both nodes
    updated.activeReactions.exists(_.nodeId == "A") shouldBe true
    updated.activeReactions.exists(_.nodeId == "B") shouldBe true
    updated.activeReactions.size shouldBe 2

  it should "not activate rules if condition is false" in:
    val rule      = ruleWithCondition(false)
    val reactions = Reactions(rules = List(rule))
    val state     = simulationState(
      time = BasicYear(Day(1), Year(2023)),
      reactions = reactions
    )
    val event   = new UpdateActiveReactionsEvent
    val updated = event.modifyFunction(state)
    updated.activeReactions shouldBe empty

  it should "not duplicate already active reactions" in:
    val rule      = ruleWithCondition(true)
    val active    = ActiveReaction(rule, "A", BasicYear(Day(1), Year(2023)))
    val reactions = Reactions(rules = List(rule), activeReactions = Set(active))
    val state     = simulationState(
      time = BasicYear(Day(2), Year(2023)),
      reactions = reactions
    )
    val event   = new UpdateActiveReactionsEvent
    val updated = event.modifyFunction(state)
    updated.activeReactions.count(_.nodeId == "A") shouldBe 1
