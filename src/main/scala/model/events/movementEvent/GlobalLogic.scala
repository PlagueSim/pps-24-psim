package model.events.movementEvent

import model.world.{Edge, EdgeType, Node, World}
import model.world.EdgeExtensions.connects
import java.util.concurrent.ThreadLocalRandom

object GlobalLogic extends MovementLogicWithEdgeCapacityAndPercentages:

  extension (world: World)
    def getAvgPopulationPerNode: Int =
      if world.nodes.isEmpty then 0
      else world.nodes.values.map(_.population).sum / world.nodes.size

  override val edgeProbabilityMap: Map[EdgeType, Double] = Map(
    EdgeType.Land -> 0.3,
    EdgeType.Sea  -> 0.2,
    EdgeType.Air  -> 0.15
  )
  override val edgeTypeCapacityMap: Map[EdgeType, Int] = Map(
    EdgeType.Land -> 500,
    EdgeType.Sea  -> 200,
    EdgeType.Air  -> 100
  )
  override def compute(
      world: World,
      percent: Double,
      rng: scala.util.Random
  ): List[(String, String, Int)] = {
    val avgPopulation = world.getAvgPopulationPerNode
    world.nodes.filter(_._2.population > 0).toList.flatMap { case (id, node) =>
      world.edges.filter(_.connects(id)).flatMap { case (_, edge) =>
        val toMove = (node.population * percent).floor.toInt
        generateMovementTuple(id, node, edge, rng, avgPopulation, toMove)
      }
    }
  }

  private def getFinalProbability(
    edgeTypology: EdgeType,
    toMove: Int,
    avgPopulation: Int
  ): Double = {
    edgeProbabilityMap.getOrElse(
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
  ): Option[(String, String, Int)] = {
    if shouldMove(edge, id, rng, avgPopulation, toMove) then
      Some(
        (
          id,
          edge.other(id).get,
          edgeTypeCapacityMap.getOrElse(edge.typology, 0).min(toMove)
        )
      )
    else None
  }
