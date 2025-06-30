package model.World

case class World private(
                          nodes: Map[String, Node],
                          edges: Set[Edge],
                          movements: Map[MovementStrategy, Double]
                        ):

  def neighbors(nodeId: String): Set[String] =
    edges.collect {
      case e if e.nodeA == nodeId => e.nodeB
      case e if e.nodeB == nodeId => e.nodeA
    }

  def areConnected(nodeA: String, nodeB: String): Boolean =
    edges.exists(e =>
      (e.nodeA == nodeA && e.nodeB == nodeB) ||
        (e.nodeA == nodeB && e.nodeB == nodeA)
    )

object World:

  def apply(
             nodes: Map[String, Node],
             edges: Set[Edge],
             movements: Map[MovementStrategy, Double]
           ): World =
    validateEdges(nodes, edges)
    validateMovements(movements)
    new World(nodes, edges, movements)

  private def validateEdges(nodes: Map[String, Node], edges: Set[Edge]): Unit =
    require(
      edges.forall(e => nodes.contains(e.nodeA) && nodes.contains(e.nodeB)),
      "Edges must connect existing nodes"
    )

  private def validateMovements(movements: Map[MovementStrategy, Double]): Unit =
    require(
      movements.nonEmpty,
      "At least one movement strategy must be defined"
    )
    require(
      movements.values.forall(_ >= 0.0),
      "Movement percentages must be non-negative"
    )
    val total = movements.values.sum
    require(
      total >= 0.999 && total <= 1.001,
      s"Movement percentages must sum to 1.0 (got $total)"
    )
