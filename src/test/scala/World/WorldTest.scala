package World

import model.World.World.*
import model.World.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WorldTest extends AnyFlatSpec with Matchers:

  "World.empty" should "start with no nodes, edges, or movement strategies" in {
    val world = World.empty
    world.nodes shouldBe empty
    world.edges shouldBe empty
    world.movements shouldBe empty
  }

  "World.apply" should "validate that edges connect existing nodes" in {
    val node = Node.withPopulation(10).build()
    val nodes = Map("A" -> node)
    val edges = Set(Edge("A", "B"))
    val movements = Map("A" -> Static)

    an[IllegalArgumentException] shouldBe thrownBy {
      World(nodes, edges, movements)
    }
  }

  it should "validate that movements target existing nodes" in {
    val node = Node.withPopulation(10).build()
    val nodes = Map("A" -> node)
    val edges = Set.empty[Edge]
    val movements = Map("B" -> Static)

    an[IllegalArgumentException] shouldBe thrownBy {
      World(nodes, edges, movements)
    }
  }

  it should "create a valid World when all nodes, edges, and movements are consistent" in {
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(5).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB
    )
    val edges = Set(
      Edge("A", "B"),
      Edge("B", "A")
    )
    val movements = Map(
      "A" -> Static,
      "B" -> Static
    )

    val world = World(nodes, edges, movements)

    world.nodes shouldBe nodes
    world.edges shouldBe edges
    world.movements shouldBe movements
  }