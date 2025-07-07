package model.World

/**
 * Represents the simulation world, containing nodes, edges, and movement strategies.
 *
 * @param nodes Map of node IDs to Node instances
 * @param edges Set of edges connecting nodes
 * @param movements Map of movement strategies and their probabilities
 */
case class World private(
                          nodes: Map[String, Node],
                          edges: Set[Edge],
                          movements: Map[MovementStrategy, Double]
                        ):

  /**
   * Returns the set of neighboring node IDs for the given node.
   */
  def neighbors(nodeId: String): Set[String] =
    edges.collect {
      case e if e.nodeA == nodeId => e.nodeB
      case e if e.nodeB == nodeId => e.nodeA
    }

  /**
   * Checks whether two nodes are connected by at least one edge of any typology.
   */
  def areConnected(nodeA: String, nodeB: String): Boolean =
    edges.exists(e =>
      (e.nodeA == nodeA && e.nodeB == nodeB) ||
        (e.nodeA == nodeB && e.nodeB == nodeA)
    )

object World:

  /**
   * Factory method that validates edges and movements.
   */
  def apply(
             nodes: Map[String, Node],
             edges: Set[Edge],
             movements: Map[MovementStrategy, Double]
           ): World =
    validateEdges(nodes, edges)
    validateMovements(movements)
    new World(nodes, edges, movements)

  /**
   * Validates that:
   * - All edges connect existing nodes.
   * - No two nodes are connected by multiple edges of the same typology.
   */
  private def validateEdges(nodes: Map[String, Node], edges: Set[Edge]): Unit =
    edgesMustConnectExistingNodes(nodes, edges)
    twoNodesCannotBeConnectedByMultipleEdgesOfSameTypology(edges)

  /**
   * Validates that:
   * - At least one movement strategy exists.
   * - All percentages are non-negative.
   * - Percentages sum to 1.0 (with tolerance).
   */
  private def validateMovements(movements: Map[MovementStrategy, Double]): Unit =
    AtLeastOneMovementStrategyExists(movements)
    movementPercentagesMustBeNonNegative(movements)
    movementPercentagesMustSumToOne(movements)

  private def twoNodesCannotBeConnectedByMultipleEdgesOfSameTypology(
    edges: Set[Edge]
  ): Unit = {
    // Group edges by node pairs and typology
    val grouped = edges.groupBy(e => (e.nodeA, e.nodeB, e.typology))
    require(
      grouped.forall { case (_, es) => es.size == 1 },
      "Two nodes cannot be connected by multiple edges of the same typology"
    )
  }

  private def edgesMustConnectExistingNodes(nodes: Map[String, Node], edges: Set[Edge]): Unit =
    require(
      edges.forall(e => nodes.contains(e.nodeA) && nodes.contains(e.nodeB)),
      "Edges must connect existing nodes"
    )




  private def AtLeastOneMovementStrategyExists(
    movements: Map[MovementStrategy, Double]
  ): Unit =
    require(
      movements.nonEmpty,
      "At least one movement strategy must be defined"
    )

  private def movementPercentagesMustBeNonNegative(
    movements: Map[MovementStrategy, Double]
  ): Unit =
    require(
      movements.values.forall(_ >= 0.0),
      "Movement percentages must be non-negative"
    )

  private def movementPercentagesMustSumToOne(
    movements: Map[MovementStrategy, Double]
  ): Unit =
    val total = movements.values.sum
    require(
      total >= 0.999 && total <= 1.001,
      s"Movement percentages must sum to 1.0 (got $total)"
    )
