package model.events.movementEvent

import model.World.{MovementStrategy, Node, RandomNeighbor, Static}

object MovementHelpers:

  private def computeDestination(
                                  strategy: model.World.MovementStrategy,
                                  nodeId: String,
                                  numPeople: Int,
                                  neighbors: String => Set[String],
                                  rng: scala.util.Random
                                ): Option[(String, Int)] =
    strategy match
      case Static =>
        // Static NON crea arrivi: restano fermi
        None
      case RandomNeighbor =>
        val neighs = neighbors(nodeId).toSeq
        if neighs.isEmpty then
          // No neighbors: restano fermi
          Some(nodeId -> numPeople)
        else
          val target = neighs(rng.nextInt(neighs.size))
          Some(target -> numPeople)
      case _ =>
        None


  def computeNodeArrivals(
                           nodeId: String,
                           node: Node,
                           movements: Map[MovementStrategy, Double],
                           neighbors: String => Set[String],
                           rng: scala.util.Random
                         ): Iterable[(String, Int)] =
    val peoplePerStrategy = MovementCalculator.movementsPerStrategy(node, movements)
    peoplePerStrategy.flatMap { case (strategy, numPeople) =>
      computeDestination(strategy, nodeId, numPeople, neighbors, rng).toList
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

    println(
      s"""
      [DEBUG] Updating node '$nodeId'
      - Original population: ${node.population}
      - Movements per strategy: $peoplePerStrategy
      - Departed (total non-static): $departed
      - Arrived: $arrived
      """
    )

    node
      .decreasePopulation(departed)
      .increasePopulation(arrived)




