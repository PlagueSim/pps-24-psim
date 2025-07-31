package model.world

object WorldValidation:

  def validateEdges(nodes: Map[String, Node], edges: Map[String, Edge]): Unit =
    edgesMustConnectExistingNodes(nodes, edges)
    noDuplicateTypologyEdges(edges)

  def validateMovements(movements: Map[MovementStrategy, Double]): Unit =
    atLeastOneStrategy(movements)
    allPercentagesNonNegative(movements)
    percentagesSumToOne(movements)

  private def edgesMustConnectExistingNodes(nodes: Map[String, Node], edges: Map[String, Edge]): Unit =
    require(
      edges.values.forall(e => nodes.contains(e.nodeA) && nodes.contains(e.nodeB)),
      "Edges must connect existing nodes"
    )

  private def noDuplicateTypologyEdges(edges: Map[String, Edge]): Unit =
    val grouped = edges.values.groupBy(e => Set(e.nodeA, e.nodeB) -> e.typology)
    require(
      grouped.forall(_._2.size == 1),
      "Two nodes cannot be connected by multiple edges of the same typology"
    )

  private def atLeastOneStrategy(movements: Map[MovementStrategy, Double]): Unit =
    require(movements.nonEmpty, "At least one movement strategy must be defined")

  private def allPercentagesNonNegative(movements: Map[MovementStrategy, Double]): Unit =
    require(
      movements.values.forall(_ >= 0.0),
      "Movement percentages must be non-negative"
    )

  private def percentagesSumToOne(movements: Map[MovementStrategy, Double]): Unit =
    val total = movements.values.sum
    require(
      total >= 0.999 && total <= 1.001,
      f"Movement percentages must sum to 1.0 (got $total%.3f)"
    )

