package model.world

import model.world.{Edge, Node, World}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import model.world.EdgeExtensions.*
import model.world.prolog.WorldConnectivity

class WorldConnectivityTest extends AnyFlatSpec with Matchers:

  "WorldConnectivity" should "correctly detect connected nodes" in {
    val nodeA = Node.withPopulation(10).build()
    val nodeB = Node.withPopulation(10).build()
    val nodeC = Node.withPopulation(10).build()
    val nodeD = Node.withPopulation(10).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB,
      "C" -> nodeC,
      "D" -> nodeD
    )

    val edges = Set(
      Edge("A", "B", EdgeType.Land),
      Edge("B", "C", EdgeType.Sea)
    ).getMapEdges

    val movements = Map.empty // non usato qui

    val world = World(nodes, edges, Map(Static -> 1.0))

    WorldConnectivity.areConnected(world, "A", "C") shouldBe false
    WorldConnectivity.areConnected(world, "A", "D") shouldBe false
    WorldConnectivity.areConnected(world, "C", "A") shouldBe false

    WorldConnectivity.areConnected(world, "B", "C") shouldBe true
    WorldConnectivity.areConnected(world, "C", "B") shouldBe true
    WorldConnectivity.areConnected(world, "A", "B") shouldBe true
    WorldConnectivity.areConnected(world, "B", "A") shouldBe true
  }

  it should "return false when no connection exists" in {
    val nodeA = Node.withPopulation(5).build()
    val nodeB = Node.withPopulation(5).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB
    )

    val edges = Map.empty[String, Edge]

    val world = World(nodes, edges, Map(Static -> 1.0))

    WorldConnectivity.areConnected(world, "A", "B") shouldBe false
  }

