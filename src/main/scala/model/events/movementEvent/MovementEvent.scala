package model.events.movementEvent

import model.world.{MovementStrategy, Node, RandomNeighbor, RandomWorld, Static, World}
import model.core.SimulationState
import model.events.Event

case class MovementEvent() extends Event[Map[String, Node]]:

  override def modifyFunction(s: SimulationState): Map[String, Node] =
    val rng = new scala.util.Random()
    val nodes = s.world.nodes
    val movements = s.world.movements
    val neighbors = s.world.neighbors
    val isEdgeOpen = s.world.isEdgeOpen

    val totalLivingPopulation = nodes.values.map(_.livingPopulation).sum

    println(totalLivingPopulation)

    val totalToMove = (totalLivingPopulation * 1.0).toInt

    val strategyToCounts = assignMoversToStrategies(totalToMove, movements)

    val allMovements = strategyToCounts.flatMap {
      case (strategy, param) if strategy != Static =>
        strategy match
          case RandomWorld =>
            MovementStrategyLogic.compute(strategy, nodes, param, neighbors, isEdgeOpen, rng)

          case RandomNeighbor =>
            val percent = movements(RandomNeighbor)
            MovementStrategyLogic.compute(strategy, nodes, percent, neighbors, isEdgeOpen, rng)

          case _ => Nil

      case _ => Nil
    }


    World.applyMovements(s.world, allMovements).nodes




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

    //val arrivals = ArrivalAggregator.computeArrivalsPerNode(nodes, movements, neighbors, isEdgeOpen, rng)

    //MovementValidator.validateDestinations(arrivals.keySet.diff(nodes.keySet))

    //NodePopulationUpdater.updateAll(nodes, arrivals, movements)