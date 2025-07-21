package model.reaction

import model.world.{World, Node, Edge, EdgeType, MovementStrategy, Static}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ReactionActionTest extends AnyFlatSpec with Matchers:

  def testWorld: World =
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(10).build()
    val edge1 = Edge("A", "B", EdgeType.Land)
    val edge2 = Edge("A", "B", EdgeType.Sea)
    World(
      Map("A" -> nodeA, "B" -> nodeB),
      Set(edge1, edge2),
      Map(Static -> 1.0)
    )

  "CloseEdges" should "close all edges of a specific type connected to a node" in:
    val world        = testWorld
    val action       = ReactionAction.CloseEdges(EdgeType.Land, "A")
    val updatedWorld = action.apply(world)
    updatedWorld.edges.count(e => e.isClose) shouldBe 1

  it should "not close edges of other types" in:
    val world        = testWorld
    val action       = ReactionAction.CloseEdges(EdgeType.Sea, "A")
    val updatedWorld = action.apply(world)
    updatedWorld.edges.count(e => e.isClose) shouldBe 1
    updatedWorld.edges.count(e =>
      e.typology == EdgeType.Land && e.isClose
    ) shouldBe 0
    updatedWorld.edges.count(e =>
      e.typology == EdgeType.Air && e.isClose
    ) shouldBe 0

  it should "reopen closed edges when reverse is called" in:
    val world        = testWorld
    val action       = ReactionAction.CloseEdges(EdgeType.Land, "A")
    val closedWorld  = action.apply(world)
    val reopenedWorld = action.reverse(closedWorld)
    reopenedWorld.edges.count(e => e.isClose) shouldBe 0

  it should "do nothing if no edges of the specified type exist" in:
    val world = testWorld
    // EdgeType.Air is not present for node "A" in testWorld
    val action = ReactionAction.CloseEdges(EdgeType.Air, "A")
    val updatedWorld = action.apply(world)
    updatedWorld.edges.count(_.isClose) shouldBe 0

  it should "do nothing if the node does not exist" in:
    val world = testWorld
    val action = ReactionAction.CloseEdges(EdgeType.Land, "Z")
    val updatedWorld = action.apply(world)
    updatedWorld.edges.count(_.isClose) shouldBe 0

  it should "not close already closed edges again" in:
    val world = testWorld.modifyEdges(testWorld.edges.map(_.close).toSet)
    val action = ReactionAction.CloseEdges(EdgeType.Land, "A")
    val updatedWorld = action.apply(world)
    updatedWorld.edges.count(_.isClose) shouldBe world.edges.size
