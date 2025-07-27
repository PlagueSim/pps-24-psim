package model.events.movementEvent

import model.world.{Edge, EdgeType, Node, World}

import java.util.concurrent.ThreadLocalRandom

object GlobalLogic extends MovementLogic:

  override def compute(
                        world: World,
                        percent: Double,
                        rng: scala.util.Random
  ): List[(String, String, Int)] =
    val edgeProbabilityMap: Map[EdgeType, Double] = Map(
      EdgeType.Land -> 0.3,
      EdgeType.Sea -> 0.2,
      EdgeType.Air -> 0.15
    )
    val edgeTypeCapacityMap: Map[EdgeType, Int] = Map(
      EdgeType.Land -> 500,
      EdgeType.Sea -> 200,
      EdgeType.Air -> 100
    )
    world.nodes.toList.flatMap { case (id, node) =>
      world.edges.filter(_._2.connects(id)).flatMap { case (_, edge) =>
        generateMovementTuple(id, node, edge, edgeProbabilityMap, edgeTypeCapacityMap)
      }
    }

  private def shouldMove(edge: Edge, nodeId: String, edgeProbabilityMap: Map[EdgeType, Double]): Boolean =
    edge.other(nodeId).isDefined && !edge.isClose && ThreadLocalRandom.current().nextDouble() < edgeProbabilityMap.getOrElse(edge.typology, 0.0)

  private def generateMovementTuple(
    id: String,
    node: Node,
    edge: Edge,
    edgeProbabilityMap: Map[EdgeType, Double],
    edgeTypeCapacityMap: Map[EdgeType, Int]
  ): Option[(String, String, Int)] = {
    if shouldMove(edge, id, edgeProbabilityMap) then
      Some((id, edge.other(id).get, edgeTypeCapacityMap.getOrElse(edge.typology, 0).min(node.population)))
    else None
  }
