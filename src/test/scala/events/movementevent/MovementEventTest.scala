package events.movementevent
import model.world.{Edge, EdgeType, MovementStrategy, Node, RandomNeighbor, Static, World}
import model.core.{SimulationEngine, SimulationState}
import model.events.movementEvent.MovementEvent
import model.events.{Event, MovementChangeInWorldEvent}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MovementEventTest extends AnyFlatSpec with Matchers:

  "MovementEvent.modifyFunction" should "update node populations correctly" in :
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

    val newWorld = for 
      e <- SimulationEngine.executeEvent(MovementEvent())
      c <- SimulationEngine.executeEvent(MovementChangeInWorldEvent(e))
    yield c

    val world2 = newWorld.runA(simulationState).value
    
    /*val updatedNodes = newWorld.modifyFunction(simulationState)
    
    val totalPopulationBefore = nodes.values.map(_.population).sum
    val totalPopulationAfter = updatedNodes.values.map(_.population).sum

    totalPopulationAfter shouldBe totalPopulationBefore

    updatedNodes.keySet should contain allOf ("A", "B")
*/
    world2.nodes("A").population should be equals 75
    world2.nodes("B").population should be equals 75
    
