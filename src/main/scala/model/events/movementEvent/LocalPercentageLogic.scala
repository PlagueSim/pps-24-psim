package model.events.movementEvent

import model.world.MovementComputation.PeopleMovement
import model.world.{Edge, Node, World}
import model.world.Types.*
object LocalPercentageLogic extends MovementLogicWithEdgeCapacityAndPercentages:

  override def edgeMovementConfig: EdgeMovementConfig = EdgeConfigurationFactory().getDefaultEdgeConfiguration

  /*
  * This logic computes movements based on a percentage of the population at each node.
  * It checks if the node has a population greater than zero and if it has open edges to neighbors.
  * If so, it generates movements to a random neighbor, respecting the edge capacity.
  * The amount of movement is calculated as a percentage of the node's population,
  * limited by the edge's capacity if specified.
  * */
  override def compute(
                        world: World,
                        percent: Percentage,
                        rng: scala.util.Random
                      ): List[PeopleMovement] =
    world.nodes
      .collect { case (id, node) if canMove(id, node, world) => (id, node) }
      .flatMap((id, node) => generateMovement(id, world, percent, rng))
      .toList

  private def canMove(id: NodeId, node: Node, world: World): Boolean =
    node.population > 0 && world.neighbors(id).exists(world.isEdgeOpen(id, _))

  private def generateMovement(
                                from: NodeId,
                                world: World,
                                percent: Percentage,
                                rng: scala.util.Random
                              ): Option[PeopleMovement] =

    val openNeighbors = world.neighbors(from).filter(world.isEdgeOpen(from, _)).toVector
    if openNeighbors.isEmpty then return None

    val to = openNeighbors(rng.nextInt(openNeighbors.size))
    val baseAmount = (world.nodes(from).population * percent).toInt.min(world.nodes(from).population)

    val edgeOpt = world.getEdges.find(e => e.connects(from) && e.other(from).contains(to))
    val finalAmount = edgeOpt
      .map(edge => baseAmount.min(edgeMovementConfig.capacity.getOrElse(edge.typology, baseAmount)))
      .getOrElse(0)

    if finalAmount <= 0 then None else Some(PeopleMovement(from, to, finalAmount))
