package model.events.movementEvent

import model.world.{MovementStrategy, Node, Static}
import model.core.SimulationState
import model.events.Event

case class MovementEvent() extends Event[Map[String, Node]]:

  private def computeTotalArrivals(
                                    nodes: Map[String, Node],
                                    movements: Map[MovementStrategy, Double],
                                    neighbors: String => Set[String],
                                    rng: scala.util.Random
                                  ): Map[String, Int] =
    val allArrivals =
      nodes.toList.flatMap { case (nodeId, node) =>
        MovementHelpers.computeNodeArrivals(nodeId, node, movements, neighbors, rng)
      }

    val grouped =
      allArrivals
        .groupBy(_._1)
        .view
        .mapValues(_.map(_._2).sum)
        .toMap

    grouped


  private def validateAllDestinationsExist(
                                            destinations: Set[String]
                                          ): Unit =
    require(
      destinations.isEmpty,
      s"Movement towards unknown nodes detected: ${destinations.mkString(", ")}"
    )

  private def updateAllNodePopulations(
                                        nodes: Map[String, Node],
                                        arrivals: Map[String, Int],
                                        movements: Map[MovementStrategy, Double]
                                      ): Map[String, Node] =
    nodes.map { case (nodeId, node) =>
      val arrived = arrivals.getOrElse(nodeId, 0)
      val peoplePerStrategy = MovementCalculator.movementsPerStrategy(node, movements)
      val departed = peoplePerStrategy.collect {
        case (strategy, num) if strategy != Static => num
      }.sum



      val updatedNode =
        node
          .decreasePopulation(departed)
          .increasePopulation(arrived)

      nodeId -> updatedNode
    }

  override def modifyFunction(s: SimulationState): Map[String, Node] =
    val rng = new scala.util.Random()
    val nodes = s.world.nodes
    val movements = s.world.movements
    val neighbors = s.world.neighbors

    val arrivals: Map[String, Int] = computeTotalArrivals(nodes, movements, neighbors, rng)

    val unknownDestinations = arrivals.keySet.diff(nodes.keySet)

    validateAllDestinationsExist(unknownDestinations)

    val updatedExistingNodes: Map[String, Node] = updateAllNodePopulations(nodes, arrivals, movements)

    updatedExistingNodes
