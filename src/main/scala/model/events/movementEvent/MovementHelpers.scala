package model.events.movementEvent

import model.world.{MovementStrategy, Node, RandomNeighbor, Static}

object MovementHelpers:

  private def computeDestination(
                                  strategy: MovementStrategy,
                                  nodeId: String,
                                  numPeople: Int,
                                  neighbors: String => Set[String],
                                  isEdgeOpen: (String, String) => Boolean,
                                  rng: scala.util.Random
                                ): Option[(String, Int)] =
    strategy match
      case Static => None
      case RandomNeighbor =>
        val validNeighbors = neighbors(nodeId).filter(n => isEdgeOpen(nodeId, n)).toSeq
        if validNeighbors.isEmpty then Some(nodeId -> numPeople)
        else Some(validNeighbors(rng.nextInt(validNeighbors.size)) -> numPeople)
      case _ => None

  def computeNodeArrivals(
                           nodeId: String,
                           node: Node,
                           movements: Map[MovementStrategy, Double],
                           neighbors: String => Set[String],
                           isEdgeOpen: (String, String) => Boolean,
                           rng: scala.util.Random
                         ): Iterable[(String, Int)] =
    val peoplePerStrategy = MovementCalculator.movementsPerStrategy(node, movements)
    peoplePerStrategy.flatMap { case (strategy, numPeople) =>
      computeDestination(strategy, nodeId, numPeople, neighbors, isEdgeOpen, rng).toList
    }

  def updateNodePopulation(
                            nodeId: String,
                            node: Node,
                            arrivals: Map[String, Int],
                            movements: Map[MovementStrategy, Double]
                          ): Node =
    val arrived = arrivals.getOrElse(nodeId, 0)
    val peoplePerStrategy = MovementCalculator.movementsPerStrategy(node, movements)
    val departed = peoplePerStrategy.collect {
      case (strategy, num) if strategy != Static => num
    }.sum

    node.decreasePopulation(departed).increasePopulation(arrived)
