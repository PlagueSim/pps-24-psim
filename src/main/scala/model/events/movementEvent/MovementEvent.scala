package model.events.movementEvent

import model.world.{MovementStrategy, Node, LocalPercentageMovement, GlobalRandomMovement, Static, World}
import model.core.SimulationState
import model.events.Event

case class MovementEvent() extends Event[Map[String, Node]]:

  override def modifyFunction(s: SimulationState): Map[String, Node] =
    val rng = new scala.util.Random()
    val nodes = s.world.nodes
    val movements = s.world.movements
    val neighbors = s.world.neighbors
    val isEdgeOpen = s.world.isEdgeOpen

    computeAllMovements(nodes, movements, neighbors, isEdgeOpen, rng, s.world)._1



  private def computeAllMovements(
                                   initialNodes: Map[String, Node],
                                   movements: Map[MovementStrategy, Double],
                                   neighbors: String => Set[String],
                                   isEdgeOpen: (String, String) => Boolean,
                                   rng: scala.util.Random,
                                   world: World
                                 ): (Map[String, Node], List[(String, String, Int)]) = {

    movements.toList.foldLeft((initialNodes, List.empty[(String, String, Int)])) {
      case ((currentNodes, collectedMoves), (strategy, percent)) =>
        val moves = MovementStrategyLogic.compute(strategy, currentNodes, percent, neighbors, isEdgeOpen, rng)
        val updatedNodes = World.applyMovements(world.modifyNodes(currentNodes), moves).nodes
        (updatedNodes, collectedMoves ++ moves)
    }
  }
