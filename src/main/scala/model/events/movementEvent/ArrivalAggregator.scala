package model.events.movementEvent

import model.world.Node
import model.world.MovementStrategy

object ArrivalAggregator:
  def compute(nodes: Map[String, Node], movements: Map[MovementStrategy, Double], neighbors: String => Set[String], isEdgeOpen: (String, String) => Boolean, rng: scala.util.Random): Map[String, Int] =
    nodes.toList
      .flatMap { case (id, node) =>
        MovementHelpers.computeNodeArrivals(id, node, movements, neighbors, isEdgeOpen, rng)
      }
      .groupBy(_._1)
      .view
      .mapValues(_.map(_._2).sum)
      .toMap

