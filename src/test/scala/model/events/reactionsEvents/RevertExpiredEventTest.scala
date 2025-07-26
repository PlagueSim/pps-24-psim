package model.events.reactionsEvents

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.core.SimulationState
import model.reaction._
import model.world.{World, Node, Edge, EdgeType, MovementStrategy, Static}
import model.time.TimeTypes.{Day, Year}
import model.time._

class RevertExpiredEventTest extends AnyFlatSpec with Matchers:
  def testWorld: World =
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(10).build()
    World(
      Map("A" -> nodeA, "B" -> nodeB),
      Map("A-B" -> Edge("A", "B", EdgeType.Land)),
      Map(Static -> 1.0)
    )

  def expiredReaction(startDay: Time, duration: Int): ActiveReaction =
    val rule = ReactionRule(
      condition = null, // not used in this test
      actionFactory =
        nodeId => ReactionAction.CloseEdges(EdgeType.Land, nodeId),
      duration = Some(duration)
    )
    ActiveReaction(rule, "A", startDay)

  "RevertExpiredEvent" should "reverse actions of expired reactions" in:
    val world       = testWorld
    val startDay    = BasicYear(Day(1), Year(2023))
    val expired     = expiredReaction(startDay, duration = 1)
    val closedWorld = expired.rule.actionFactory("A").apply(world)
    val state       = SimulationState(
      BasicYear(Day(2), Year(2023)), // current day > startDay + duration
      null,
      null,
      closedWorld,
      null,
      null,
      Reactions(activeReactions = Set(expired))
    )
    val event         = RevertExpiredEvent()
    val revertedWorld = event.modifyFunction(state)
    revertedWorld.edges.forall(!_._2.isClose) shouldBe true

  it should "not revert non expired reactions" in:
    val world       = testWorld
    val startDay    = BasicYear(Day(1), Year(2023))
    val active      = expiredReaction(startDay, duration = 5)
    val closedWorld = active.rule.actionFactory("A").apply(world)
    val state       = SimulationState(
      BasicYear(Day(2), Year(2023)), // current day < startDay + duration
      null,
      null,
      closedWorld,
      null,
      null,
      Reactions(activeReactions = Set(active))
    )
    val event         = RevertExpiredEvent()
    val revertedWorld = event.modifyFunction(state)
    // Should not revert, edge should remain closed
    revertedWorld.edges.forall(_._2.isClose) shouldBe true

  it should "do nothing if there are no active reactions" in:
    val world = testWorld
    val state = SimulationState(
      BasicYear(Day(2), Year(2023)),
      null,
      null,
      world,
      null,
      null,
      Reactions(activeReactions = Set.empty)
    )
    val event         = RevertExpiredEvent()
    val revertedWorld = event.modifyFunction(state)
    revertedWorld shouldBe world
