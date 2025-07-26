package model.events.movementEvent

import model.world.Node
import model.world.MovementStrategy

object ArrivalAggregator:
  def computeArrivalsPerNode(
                              nodes: Map[String, Node],
                              movements: Map[MovementStrategy, Double],
                              neighbors: String => Set[String],
                              isEdgeOpen: (String, String) => Boolean,
                              rng: scala.util.Random
                            ): Map[String, Int] =
    val individualArrivals = nodes.toList.flatMap:
      case (id, node) => MovementHelpers.computeNodeArrivals(id, node, movements, neighbors, isEdgeOpen, rng)
    
    groupAndSumArrivals(individualArrivals)

  private def groupAndSumArrivals(arrivals: List[(String, Int)]): Map[String, Int] =
    arrivals
      .groupBy(_._1)
      .view
      .mapValues(_.map(_._2).sum)
      .toMap


