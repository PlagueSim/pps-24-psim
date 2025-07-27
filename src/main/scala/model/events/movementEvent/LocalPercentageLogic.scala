package model.events.movementEvent

import model.world.{Edge, Node, World}

object LocalPercentageLogic extends MovementLogic:

  def compute(
               world: World,
               percent: Double,
               rng: scala.util.Random
             ): List[(String, String, Int)] =
    world.nodes.toList
      .filter(canMove(_, world.neighbors, world.isEdgeOpen))
      .flatMap(generateMovements(_, percent, world.neighbors, world.isEdgeOpen, rng))

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
                               ): List[(String, String, Int)] =
    val (from, node) = entry
    val openNeighbors = neighbors(from).filter(isEdgeOpen(from, _)).toVector
    val toMove = (node.population * percent).toInt.min(node.population)
    val to = openNeighbors(rng.nextInt(openNeighbors.size))
    List((from, to, toMove))