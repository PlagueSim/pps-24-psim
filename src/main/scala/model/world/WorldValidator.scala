package model.world
import Types.*
object WorldValidator:

  /**
   * Validates the nodes in the world.
   * Ensures that each node has a unique ID and a valid position.
   *
   * @param nodes Map of node IDs to Node objects
   * @param edges Map of edge IDs to Edge objects
   *              
   * @throws IllegalArgumentException if any edge connects non-existing nodes
   * @throws IllegalArgumentException if there are duplicate edges of the same typology between two nodes
   */
  def validateEdges(nodes: Map[NodeId, Node], edges: Map[EdgeId, Edge]): Unit =
    edgesMustConnectExistingNodes(nodes, edges)
    noDuplicateTypologyEdges(edges)

  /**
   * Validates the movement strategies defined in the world.
   * 
   * @param movements Map of MovementStrategy to their corresponding percentages
   *                  
   * @throws IllegalArgumentException if no movement strategies are defined
   * @throws IllegalArgumentException if any movement percentage is negative
   * @throws IllegalArgumentException if the sum of movement percentages is not approximately 1.0
   */
  def validateMovements(movements: Map[MovementStrategy, Percentage]): Unit =
    atLeastOneStrategy(movements)
    allPercentagesNonNegative(movements)
    percentagesSumToOne(movements)

  private def edgesMustConnectExistingNodes(nodes: Map[NodeId, Node], edges: Map[EdgeId, Edge]): Unit =
    require(
      edges.values.forall(e => nodes.contains(e.nodeA) && nodes.contains(e.nodeB)),
      "Edges must connect existing nodes"
    )

  private def noDuplicateTypologyEdges(edges: Map[EdgeId, Edge]): Unit =
    val grouped = edges.values.groupBy(e => Set(e.nodeA, e.nodeB) -> e.typology)
    require(
      grouped.forall(_._2.size == 1),
      "Two nodes cannot be connected by multiple edges of the same typology"
    )

  private def atLeastOneStrategy(movements: Map[MovementStrategy, Percentage]): Unit =
    require(movements.nonEmpty, "At least one movement strategy must be defined")

  private def allPercentagesNonNegative(movements: Map[MovementStrategy, Percentage]): Unit =
    require(
      movements.values.forall(_ >= 0.0),
      "Movement percentages must be non-negative"
    )

  private def percentagesSumToOne(movements: Map[MovementStrategy, Percentage]): Unit =
    val total = movements.values.sum
    require(
      total >= 0.999 && total <= 1.001,
      f"Movement percentages must sum to 1.0 (got $total%.3f)"
    )

