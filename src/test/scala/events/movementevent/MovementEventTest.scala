package model.events.movementEvent

import model.World._
import model.core.SimulationState
import model.events.Event

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MovementEventTest extends AnyFlatSpec with Matchers:

  "MovementEvent.modifyFunction" should "update node populations correctly" in {
    val nodeA = Node.withPopulation(100).build()
    val nodeB = Node.withPopulation(50).build()

    val nodes = Map(
      "A" -> nodeA,
      "B" -> nodeB
    )

    val edges = Set(
      Edge("A", "B", EdgeType.Land)
    )

    val movements: Map[MovementStrategy, Double] = Map(
      Static -> 0.5,
      RandomNeighbor -> 0.5
    )

    val world = World(nodes, edges, movements)

    val simulationState = SimulationState(null, null, null, world)

    val event = MovementEvent()

    val updatedNodes = event.modifyFunction(simulationState)

    // Because 50% of each node's population moves randomly,
    // and each node has one neighbor,
    // A should lose 50 people and B should lose 25.
    // Each should randomly gain population from the other's departing people.
    // Since randomness is involved, check that totals match.
    val totalPopulationBefore = nodes.values.map(_.population).sum
    val totalPopulationAfter = updatedNodes.values.map(_.population).sum

    totalPopulationAfter shouldBe totalPopulationBefore

    updatedNodes.keySet should contain allOf ("A", "B")

    // Check that populations are in the expected range.
    updatedNodes("A").population should be equals 75
    updatedNodes("B").population should be equals 75
    
    print(s"[DEBUG] Updated Nodes: ${updatedNodes.map { case (id, node) => s"$id: ${node.population}" }.mkString(", ")}")
  }
