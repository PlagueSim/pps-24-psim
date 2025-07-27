package model.events.movementEvent

import model.world.Node

object LocalPercentageLogic:

  def compute(
               nodes: Map[String, Node],
               percent: Double,
               neighbors: String => Set[String],
               isEdgeOpen: (String, String) => Boolean,
               rng: scala.util.Random
             ): List[(String, String)] =
    nodes.toList
      .filter(canMove(_, neighbors, isEdgeOpen))
      .flatMap(generateMovements(_, percent, neighbors, isEdgeOpen, rng))

  private def canMove(
                       entry: (String, Node),
                       neighbors: String => Set[String],
                       isEdgeOpen: (String, String) => Boolean
                     ): Boolean =
    val (id, node) = entry
    node.population > 0 && neighbors(id).exists(isEdgeOpen(id, _))

  private def generateMovements(
                                 entry: (String, Node),
                                 percent: Double,
                                 neighbors: String => Set[String],
                                 isEdgeOpen: (String, String) => Boolean,
                                 rng: scala.util.Random
                               ): List[(String, String)] =
    val (from, node) = entry
    val openNeighbors = neighbors(from).filter(isEdgeOpen(from, _)).toVector
    val toMove = (node.population * percent).toInt.min(node.population)
    (1 to toMove).map { _ =>
      val to = openNeighbors(rng.nextInt(openNeighbors.size))
      (from, to)
    }.toList
