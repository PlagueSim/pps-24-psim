package model.events.movementEvent

import model.world.MovementComputation.PeopleMovement
import model.world.{Edge, Node, World}

object LocalPercentageLogic extends MovementLogic:

  def compute(
               world: World,
               percent: Double,
               rng: scala.util.Random
             ): List[PeopleMovement] =
    world.nodes.toList
      .filter(canMove(_, world))
      .flatMap(generateMovements(_, percent, world, rng))

  private def canMove(
                       entry: (String, Node),
                       world: World
                     ): Boolean =
    val (id, node) = entry
    node.population > 0 && world.neighbors(id).exists(world.isEdgeOpen(id, _))

  private def generateMovements(
                                 entry: (String, Node),
                                 percent: Double,
                                 world: World,
                                 rng: scala.util.Random
                               ): List[PeopleMovement] =
    val (from, node) = entry
    val openNeighbors = world.neighbors(from).filter(world.isEdgeOpen(from, _)).toVector
    val toMove = (node.population * percent).toInt.min(node.population)
    val to = openNeighbors(rng.nextInt(openNeighbors.size))
    List(PeopleMovement(from, to, toMove))