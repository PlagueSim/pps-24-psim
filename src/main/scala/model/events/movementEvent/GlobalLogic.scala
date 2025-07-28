package model.events.movementEvent

import model.world.{Edge, EdgeType, Node, World}
import model.world.EdgeExtensions.connects
import java.util.concurrent.ThreadLocalRandom

object GlobalLogic extends MovementLogicWithEdgeCapacityAndPercentages:
  override val edgeProbabilityMap: Map[EdgeType, Double] = Map(
    EdgeType.Land -> 0.3,
    EdgeType.Sea -> 0.2,
    EdgeType.Air -> 0.15
  )
  override val edgeTypeCapacityMap: Map[EdgeType, Int] = Map(
    EdgeType.Land -> 500,
    EdgeType.Sea -> 200,
    EdgeType.Air -> 100
  )
  override def compute(
                        world: World,
                        percent: Double,
                        rng: scala.util.Random
  ): List[(String, String, Int)] =

    world.nodes.toList.flatMap {
      case (id, node) =>
      world.edges.filter(_.connects(id)).flatMap { case (_, edge) =>
        generateMovementTuple(id, node, edge, rng)
      }
    }

  private def shouldMove(edge: Edge, nodeId: String, rng: scala.util.Random): Boolean =
    edge.other(nodeId).isDefined && !edge.isClose && rng.nextDouble() < edgeProbabilityMap.getOrElse(edge.typology, 0.0)

  private def generateMovementTuple(
    id: String,
    node: Node,
    edge: Edge,
    rng: scala.util.Random                               
  ): Option[(String, String, Int)] = {
    if shouldMove(edge, id, rng) then
      Some((id, edge.other(id).get, edgeTypeCapacityMap.getOrElse(edge.typology, 0).min(node.population)))
    else None
  }

