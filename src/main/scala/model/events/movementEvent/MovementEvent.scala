package model.events.movementEvent

import model.world.{MovementStrategy, Node, Static}
import model.core.SimulationState
import model.events.Event

case class MovementEvent() extends Event[Map[String, Node]]:

  override def modifyFunction(s: SimulationState): Map[String, Node] =
    val rng = new scala.util.Random()
    val nodes = s.world.nodes
    val movements = s.world.movements
    val neighbors = s.world.neighbors
    val isEdgeOpen = s.world.isEdgeOpen

    val arrivals = ArrivalAggregator.compute(nodes, movements, neighbors, isEdgeOpen, rng)

    MovementValidator.validateDestinations(arrivals.keySet.diff(nodes.keySet))

    NodePopulationUpdater.updateAll(nodes, arrivals, movements)