package model.world
import model.events.movementEvent
import model.events.movementEvent.MovementEvent
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MovementTest extends AnyFlatSpec with Matchers:
  "" should "" in {

    val node1 = Node.withPopulation(49).build()
    val node2 = Node.withPopulation(10).build()

    val nodes = Map(
      "A" -> node1,
      "B" -> node2
    )

    val edge = Map("A-B" -> Edge("A", "B", EdgeType.Land))


    val movementStrategies = Map[MovementStrategy, Double](
      Static -> 1.0,
    )

    val world = World(nodes, edge, movementStrategies)

    val updateWorld = World.applyMovements(world, List(("A", "B", 2)))

    assert(updateWorld.nodes("A").population == 47)

    assert(updateWorld.nodes("B").population == 12)
  }



