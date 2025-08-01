package model.events.movementEvent

import model.world.MovementComputation.PeopleMovement
import model.world.{Edge, Node, World}

object LocalPercentageLogic extends MovementLogicWithEdgeCapacityAndPercentages:

  override def edgeMovementConfig: EdgeMovementConfig = EdgeConfigurationFactory().getDefaultEdgeConfiguration

  override def compute(
                        world: World,
                        percent: Double,
                        rng: scala.util.Random
                      ): List[PeopleMovement] =
    world.nodes
      .collect { case (id, node) if canMove(id, node, world) => (id, node) }
      .flatMap((id, node) => generateMovement(id, node, world, percent, rng))
      .toList

  private def canMove(id: String, node: Node, world: World): Boolean =
    node.population > 0 && world.neighbors(id).exists(world.isEdgeOpen(id, _))

  private def generateMovement(
                                from: String,
                                node: Node,
                                world: World,
                                percent: Double,
                                rng: scala.util.Random
                              ): Option[PeopleMovement] =
    
    val openNeighbors = world.neighbors(from).filter(world.isEdgeOpen(from, _)).toVector
    if openNeighbors.isEmpty then return None

    val to = openNeighbors(rng.nextInt(openNeighbors.size))
    val baseAmount = (node.population * percent).toInt.min(node.population)

    val edgeOpt = world.getEdges.find(e => e.connects(from) && e.other(from).contains(to))
    val finalAmount = edgeOpt
      .map(edge => baseAmount.min(edgeMovementConfig.capacity.getOrElse(edge.typology, baseAmount)))
      .getOrElse(0)

    Some(PeopleMovement(from, to, finalAmount))
