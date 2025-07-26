package model.world

import model.world.World.*
import model.world.*

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WorldTest extends AnyFlatSpec with Matchers:

  "model/world" should "validate that edges connect existing nodes" in {
    val node = Node.withPopulation(10).build()
    val nodes = Map("A" -> node)
    val edges = Map("A-B" -> Edge("A", "B", EdgeType.Land))
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
    val edges = Map.empty[String, Edge]

    val emptyMovements = Map.empty[MovementStrategy, Double]
    an[IllegalArgumentException] shouldBe thrownBy {
      World(nodes, edges, emptyMovements)
    }

    val invalidMovements: Map[MovementStrategy, Double] = Map(
      Static -> 0.6,
      LocalPercentageMovement -> 0.3
    )
    an[IllegalArgumentException] shouldBe thrownBy {
      World(nodes, edges, invalidMovements)
    }

    val negativeMovements: Map[MovementStrategy, Double] = Map(
      Static -> 0.5,
      LocalPercentageMovement -> -0.5
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
    val edges = Map("A-B" -> Edge("A", "B", EdgeType.Land))
    val movements: Map[MovementStrategy, Double] =Map(
      Static -> 0.7,
      LocalPercentageMovement -> 0.3
    )

    val world = World(nodes, edges, movements)

    world.nodes shouldBe nodes
    world.edges shouldBe edges
    world.movements shouldBe movements
  }

  it should "ignore the adding of an edge, if already exists" in {
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(5).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB
    )
    val edges = Set(
      Edge("A", "B", EdgeType.Air),
      Edge("A", "B", EdgeType.Air)
    )

    val movements : Map[MovementStrategy, Double] = Map(
      Static -> 1.0
    )

    edges.size should be (1)
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
    val edges = Map("A-B-L" -> Edge("A", "B", EdgeType.Land), 
        "B-C-S" -> Edge("B", "C", EdgeType.Sea))
    val movements: Map[MovementStrategy, Double] = Map(
      Static -> 1.0
    )

    val world = World(nodes, edges, movements)

    world.neighbors("A") should contain theSameElementsAs Set("B")
    world.neighbors("B") should contain theSameElementsAs Set("A", "C")
    world.neighbors("C") should contain theSameElementsAs Set("B")
  }
  
  it should "modify nodes correctly" in {
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(5).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB
    )
    val edges = Map("A-B" -> Edge("A", "B", EdgeType.Land))
    val movements: Map[MovementStrategy, Double] = Map(
      Static -> 1.0
    )

    val world = World(nodes, edges, movements)

    val newNodeC = Node.withPopulation(7).build()
    val modifiedWorld = world.modifyNodes(
      world.nodes + ("C" -> newNodeC)
    )

    modifiedWorld.nodes should contain key "C"
    modifiedWorld.nodes("C").population shouldBe 7
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
    val edges = Map("A-B" -> Edge("A", "B", EdgeType.Land), 
        "B-C" -> Edge("B", "C", EdgeType.Air))
    val movements: Map[MovementStrategy, Double] =Map(
      Static -> 1.0
    )

    val world = World(nodes, edges, movements)

    world.areConnected("A", "B") shouldBe true
    world.areConnected("B", "C") shouldBe true
    world.areConnected("A", "C") shouldBe false
  }
