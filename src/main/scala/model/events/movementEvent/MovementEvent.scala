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

    val totalPopulation = nodes.values.map(_.population).sum

    println(totalPopulation)


    val (finalNodes, allMovements) = movements.toList.foldLeft((nodes, List.empty[(String, String)])) {
      case ((currentNodes, accMoves), (strategy, param)) =>
        val moves = MovementStrategyLogic.compute(strategy, currentNodes, param, neighbors, isEdgeOpen, rng)
        val updatedNodes = World.applyMovements(s.world.modifyNodes(currentNodes), moves).nodes
        (updatedNodes, accMoves ++ moves)
    }
    
    finalNodes



  private def assignMoversToStrategies(
                                              totalToMove: Int,
                                              movements: Map[MovementStrategy, Double]
                                            ): List[(MovementStrategy, Int)] =
    val initialAssignments = movements.toList.map { case (strategy, percent) =>
      strategy -> (percent * totalToMove).toInt
    }

    val assignedTotal = initialAssignments.map(_._2).sum
    val missing = totalToMove - assignedTotal

    val adjustedAssignments = initialAssignments.zipWithIndex.map {
      case ((strategy, count), idx) if idx < missing => strategy -> (count + 1)
      case ((strategy, count), _) => strategy -> count
    }

    adjustedAssignments.filter(_._2 > 0)
