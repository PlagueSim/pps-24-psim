package events.movementevent
import model.world.*
import model.core.{SimulationEngine, SimulationState}
import model.events.movementEvent.MovementEvent
import model.events.ChangeNodesInWorldEvent
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

    val edges = Set(Edge("A", "B", EdgeType.Land))

    val movements: Map[MovementStrategy, Double] = Map(
      Static -> 0.5,
      RandomNeighbor -> 0.5
    )

    val worldBefore = World(nodes, edges, movements)

    val simulationState = SimulationState(null, null, null, worldBefore, null, null)

    val newWorld = for 
      e <- SimulationEngine.executeEvent(MovementEvent())
      c <- SimulationEngine.executeEvent(ChangeNodesInWorldEvent(e))
    yield c

    val worldAfter = newWorld.runA(simulationState).value
    
    val totalPopulationBefore = worldBefore.nodes.values.map(_.population).sum
    val totalPopulationAfter = worldAfter.nodes.values.map(_.population).sum

    totalPopulationAfter shouldBe totalPopulationBefore
    worldAfter.nodes("A").population should be equals 75
    worldAfter.nodes("B").population should be equals 75
    
