package model.events.movementEvent

import model.world.{Edge, EdgeType, Node, World}
import model.world.EdgeExtensions.connects
import model.world.MovementComputation.PeopleMovement

import java.util.concurrent.ThreadLocalRandom

object GlobalLogic extends MovementLogicWithEdgeCapacityAndPercentages:

  extension (world: World)
    private def getAvgPopulationPerNode: Int =
      if world.nodes.isEmpty then 0
      else world.nodes.values.map(_.population).sum / world.nodes.size
  
  override def edgeMovementConfig: EdgeMovementConfig = EdgeConfigurationFactory().getDefaultEdgeConfiguration

  override def compute(
                        world: World,
                        percent: Double,
                        rng: scala.util.Random
                      ): Iterable[PeopleMovement] = {
    val avgPopulation = world.getAvgPopulationPerNode
    
    for {
      (id, node) <- world.nodes if node.population > 0
      (_, edge) <- world.edges if edge.connects(id)
      toMove = (node.population * percent).floor.toInt
      move <- generateMovementTuple(id, node, edge, rng, avgPopulation, toMove)
    } yield move
  }


  private def getFinalProbability(
    edgeTypology: EdgeType,
    toMove: Int,
    avgPopulation: Int
  ): Double = {
    edgeMovementConfig.probability.getOrElse(
      edgeTypology,
      0.0
    ) * (toMove.toDouble / avgPopulation)
  }

  private def shouldMove(
      edge: Edge,
      nodeId: String,
      rng: scala.util.Random,
      avgPopulation: Int,
      toMove: Int
  ): Boolean = {
    val finalProbability = getFinalProbability(
      edge.typology,
      toMove,
      avgPopulation
    )
    edge.other(nodeId).isDefined && !edge.isClose && rng
      .nextDouble() < finalProbability
  }

  private def generateMovementTuple(
      id: String,
      node: Node,
      edge: Edge,
      rng: scala.util.Random,
      avgPopulation: Int,
      toMove: Int
  ): Option[PeopleMovement] = {
    if shouldMove(edge, id, rng, avgPopulation, toMove) then
      Some(
        PeopleMovement(
          id,
          edge.other(id).get,
          edgeMovementConfig.capacity.getOrElse(edge.typology, 0).min(toMove)
        )
      )
    else None
  }
