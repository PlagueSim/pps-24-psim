package model.world

import model.World.World.*
import model.World.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WorldTest extends AnyFlatSpec with Matchers:

  "model/world" should "validate that edges connect existing nodes" in {
    val node = Node.withPopulation(10).build()
    val nodes = Map("A" -> node)
    val edges = Set(Edge("A", "B", EdgeType.Land))
    val movements: Map[MovementStrategy, Double] = Map(
      Static -> 1.0
    )

    an[IllegalArgumentException] shouldBe thrownBy {
      World(nodes, edges, movements)
    }
  }

  it should "validate that movement percentages are non-empty and sum to 1.0" in {
    val node = Node.withPopulation(10).build()
    val nodes = Map("A" -> node)
    val edges = Set.empty[Edge]

    val emptyMovements = Map.empty[MovementStrategy, Double]
    an[IllegalArgumentException] shouldBe thrownBy {
      World(nodes, edges, emptyMovements)
    }

    val invalidMovements: Map[MovementStrategy, Double] = Map(
      Static -> 0.6,
      RandomNeighbor -> 0.3
    )
    an[IllegalArgumentException] shouldBe thrownBy {
      World(nodes, edges, invalidMovements)
    }

    val negativeMovements: Map[MovementStrategy, Double] = Map(
      Static -> 0.5,
      RandomNeighbor -> -0.5
    )
    an[IllegalArgumentException] shouldBe thrownBy {
      World(nodes, edges, negativeMovements)
    }
  }

  it should "create a valid World when all nodes, edges, and global movement percentages are correct" in {
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(5).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB
    )
    val edges = Set(
      Edge("A", "B", EdgeType.Land)
    )
    val movements: Map[MovementStrategy, Double] =Map(
      Static -> 0.7,
      RandomNeighbor -> 0.3
    )

    val world = World(nodes, edges, movements)

    world.nodes shouldBe nodes
    world.edges shouldBe edges
    world.movements shouldBe movements
  }

  it should "not allow multiple edges of the same typology between the same nodes" in {
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(5).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB
    )
    val edges = Set(
      Edge("A", "B", EdgeType.Air, weight = 1.0),
      Edge("A", "B", EdgeType.Air, weight = 2.0)
    )
    val movements : Map[MovementStrategy, Double] = Map(
      Static -> 1.0
    )

    an[IllegalArgumentException] shouldBe thrownBy {
      World(nodes, edges, movements)
    }
  }

  it should "allow neighbors to be retrieved correctly" in {
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(5).build()
    val nodeC = Node.withPopulation(3).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB,
      "C" -> nodeC
    )
    val edges = Set(
      Edge("A", "B", EdgeType.Land),
      Edge("B", "C", EdgeType.Sea)
    )
    val movements: Map[MovementStrategy, Double] = Map(
      Static -> 1.0
    )

    val world = World(nodes, edges, movements)

    world.neighbors("A") should contain theSameElementsAs Set("B")
    world.neighbors("B") should contain theSameElementsAs Set("A", "C")
    world.neighbors("C") should contain theSameElementsAs Set("B")
  }

  it should "check if two nodes are connected" in {
    val nodeA = Node.withPopulation(1).build()
    val nodeB = Node.withPopulation(2).build()
    val nodeC = Node.withPopulation(3).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB,
      "C" -> nodeC
    )
    val edges = Set(
      Edge("A", "B", EdgeType.Land),
      Edge("B", "C", EdgeType.Air)
    )
    val movements: Map[MovementStrategy, Double] =Map(
      Static -> 1.0
    )

    val world = World(nodes, edges, movements)

    world.areConnected("A", "B") shouldBe true
    world.areConnected("B", "C") shouldBe true
    world.areConnected("A", "C") shouldBe false
  }
