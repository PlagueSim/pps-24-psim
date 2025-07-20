package model.events.reactionsEvents

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.core.SimulationState
import model.reaction._
import model.world.{World, Node, Edge, EdgeType, Static}
import model.time.TimeTypes.{Day, Year}
import model.time._

class ApplyReactionsEventTest extends AnyFlatSpec with Matchers:
  def testWorld: World =
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(10).build()
    val edge  = Edge("A", "B", EdgeType.Land)
    World(
      Map("A" -> nodeA, "B" -> nodeB),
      Set(edge),
      Map(Static -> 1.0)
    )

  def simulationState(
      world: World = testWorld,
      reactions: Reactions = Reactions()
  ): SimulationState =
    SimulationState(
      BasicYear(Day(0), Year(2023)),
      null,
      null,
      world,
      null,
      null,
      reactions
    )

  trait ConditionFactory {
    def alwaysTrue: ReactionCondition = new ReactionCondition {
      override def isSatisfied(
          state: SimulationState,
          nodeId: String
      ): Boolean = true
    }
    def alwaysFalse: ReactionCondition = new ReactionCondition {
      override def isSatisfied(
          state: SimulationState,
          nodeId: String
      ): Boolean = false
    }
  }
  object ConditionFactory extends ConditionFactory

  "ApplyReactionsEvent" should "apply all active reactions to the world" in:
    val world = testWorld
    val rule  = ReactionRule(
      condition = ConditionFactory.alwaysTrue,
      actionFactory = nodeId => ReactionAction.CloseEdges(EdgeType.Land, nodeId)
    )
    val activeA   = ActiveReaction(rule, "A", BasicYear(Day(0), Year(2023)))
    val activeB   = ActiveReaction(rule, "B", BasicYear(Day(0), Year(2023)))
    val reactions =
      Reactions(rules = List(rule), activeReactions = Set(activeA, activeB))
    val state        = simulationState(world, reactions)
    val event        = ApplyReactionsEvent()
    val updatedWorld = event.modifyFunction(state)
    // Both edges should be closed
    updatedWorld.edges.forall(_.isClose) shouldBe true

  it should "do nothing if there are no active reactions" in:
    val world        = testWorld
    val reactions    = Reactions()
    val state        = simulationState(world, reactions)
    val event        = ApplyReactionsEvent()
    val updatedWorld = event.modifyFunction(state)
    updatedWorld shouldBe world