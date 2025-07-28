package model.events.movementEvent

import model.world.{Edge, GlobalRandomMovement, LocalPercentageMovement, MovementStrategy, Node, Static, World}
import model.core.SimulationState
import model.events.Event

case class MovementEvent() extends Event[Map[String, Node]]:

  override def modifyFunction(s: SimulationState): Map[String, Node] =
    val rng = new scala.util.Random()
    val nodes = s.world.nodes
    val movements = s.world.movements
    val neighbors = s.world.neighbors
    val isEdgeOpen = s.world.isEdgeOpen

    computeAllMovements(rng, s.world)._1



  private def computeAllMovements(
                                   rng: scala.util.Random,
                                   world: World
                                 ): (Map[String, Node], List[(String, String, Int)]) = {

    world.movements.toList.foldLeft((world.nodes, List.empty[(String, String, Int)])) {
      case ((currentNodes, collectedMoves), (strategy, percent)) =>
        val moves = MovementStrategyLogic.compute(world, strategy,percent, rng)
        val updatedNodes = World.applyMovements(world.modifyNodes(currentNodes), moves).nodes
        (updatedNodes, collectedMoves ++ moves)
    }
  }
