package model.events.movementEvent

import model.world.{Edge, EdgeType, Node, World}
import model.world.EdgeExtensions.connects
import model.world.MovementComputation.PeopleMovement

import java.util.concurrent.ThreadLocalRandom

object GlobalLogic extends MovementLogicWithEdgeCapacityAndPercentages:


  override def edgeMovementConfig: EdgeMovementConfig = EdgeConfigurationFactory().getDefaultEdgeConfiguration

  /*
  * This logic computes the movement of people across edges in a global context.
  * It considers the average population per node and the capacity of edges.
  * The movement is determined by the percentage of the population that should move,
  * the edge's typology, and a random factor.

  * It generates a collection of `PeopleMovement` instances representing the movements.
  * Each movement is only created if the edge is open, the node has a population,
  * and the random factor meets the probability criteria defined in the edge movement configuration.
  * */
  override def compute(
                        world: World,
                        percent: Double,
                        rng: scala.util.Random
                      ): Iterable[PeopleMovement] =
    val avgPopulation = world.getAvgPopulationPerNode

    for {
      (id, node) <- world.nodes if node.population > 0
      (_, edge) <- world.edges if edge.connects(id) && !edge.isClose
      toMove = (node.population * percent).floor.toInt
      move <- generateMovementTuple(id, edge, rng, avgPopulation, toMove)
    } yield move


  private def getFinalProbability(
    edgeTypology: EdgeType,
    toMove: Int,
    avgPopulation: Int
  ): Double =
    edgeMovementConfig.probability.getOrElse(
      edgeTypology,
      0.0
    ) * (toMove.toDouble / avgPopulation)

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
      from: String,
      edge: Edge,
      rng: scala.util.Random,
      avgPopulation: Int,
      toMove: Int
  ): Option[PeopleMovement] =
    val finalAmount = edgeMovementConfig.capacity.getOrElse(edge.typology, 0).min(toMove)
    if finalAmount <= 0 then return None
    if shouldMove(edge, from, rng, avgPopulation, toMove) then
      Some(PeopleMovement(
          from,
          edge.other(from).get,
          finalAmount))
    else None
