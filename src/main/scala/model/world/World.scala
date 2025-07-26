package model.world

case class World private (
                           nodes: Map[String, Node],
                           edges: Map[String, Edge],
                           movements: Map[MovementStrategy, Double]
                         ):
  /* Returns a new World instance with updated nodes */
  def modifyNodes(newNodes: Map[String, Node]): World = copy(nodes = newNodes)

  /* Returns a new World instance with updated edges */
  def modifyEdges(newEdges: Map[String, Edge]): World = copy(edges = newEdges)

  /* Returns a new World instance with updated movement strategies */
  def modifyMovements(newMovements: Map[MovementStrategy, Double]): World = copy(movements = newMovements)

  /* Returns all edges in the world */
  def getEdges: Iterable[Edge] =
    edges.values

  /* Returns the set of neighboring node IDs for the given node ID */
  def neighbors(nodeId: String): Set[String] =
    edges.values.collect {
      case e if e.nodeA == nodeId => e.nodeB
      case e if e.nodeB == nodeId => e.nodeA
    }.toSet

  /* Returns true if there is at least one edge connecting the two given nodes */
  def areConnected(nodeA: String, nodeB: String): Boolean =
    edges.values.exists(e =>
      (e.nodeA == nodeA && e.nodeB == nodeB) ||
        (e.nodeA == nodeB && e.nodeB == nodeA)
    )

object World:
  /* Creates a World instance after validating edges and movement strategies */
  def apply(
             nodes: Map[String, Node],
             edges: Map[String, Edge],
             movements: Map[MovementStrategy, Double]
           ): World =
    validateEdges(nodes, edges)
    validateMovements(movements)
    new World(nodes, edges, movements)

  def applyMovements(
                      world: World,
                      movements: List[(String, String)]
                    ): World =
    val updatedNodes = movements.foldLeft(world.nodes):
      case (acc, (from, to)) =>
      acc
        .updated(from, acc(from).decreasePopulation(1))
        .updated(to, acc(to).increasePopulation(1))

    world.copy(nodes = updatedNodes)


  private def validateEdges(nodes: Map[String, Node], edges: Map[String, Edge]): Unit =
    edgesMustConnectExistingNodes(nodes, edges)
    twoNodesCannotBeConnectedByMultipleEdgesOfSameTypology(edges)

  private def validateMovements(movements: Map[MovementStrategy, Double]): Unit =
    AtLeastOneMovementStrategyExists(movements)
    movementPercentagesMustBeNonNegative(movements)
    movementPercentagesMustSumToOne(movements)

  private def twoNodesCannotBeConnectedByMultipleEdgesOfSameTypology(edges: Map[String, Edge]): Unit =
    val grouped = edges.values.groupBy(e => Set(e.nodeA, e.nodeB) -> e.typology)
    require(
      grouped.forall(_._2.size == 1),
      "Two nodes cannot be connected by multiple edges of the same typology"
    )

  private def edgesMustConnectExistingNodes(nodes: Map[String, Node], edges: Map[String, Edge]): Unit =
    require(
      edges.values.forall(e => nodes.contains(e.nodeA) && nodes.contains(e.nodeB)),
      "Edges must connect existing nodes"
    )

  private def AtLeastOneMovementStrategyExists(movements: Map[MovementStrategy, Double]): Unit =
    require(movements.nonEmpty, "At least one movement strategy must be defined")

  private def movementPercentagesMustBeNonNegative(movements: Map[MovementStrategy, Double]): Unit =
    require(
      movements.values.forall(_ >= 0.0),
      "Movement percentages must be non-negative"
    )

  private def movementPercentagesMustSumToOne(movements: Map[MovementStrategy, Double]): Unit =
    val total = movements.values.sum
    require(
      total >= 0.999 && total <= 1.001,
      s"Movement percentages must sum to 1.0 (got $total)"
    )

  extension (world: World)
    def addNode(id: String, data: Node): World =
      world.modifyNodes(world.nodes + (id -> data))

    def removeNode(id: String): World =
      val updatedEdges = world.edges.filterNot { case (_, edge) => edge.connects(id) }
      val updatedNodes = world.nodes - id
      world.modifyNodes(updatedNodes).modifyEdges(updatedEdges)

    def movePeople(from: String, to: String, amount: Int): World =
      (for
        fromNode <- world.nodes.get(from)
        toNode <- world.nodes.get(to)
      yield
        val fromUpdated = fromNode.decreasePopulation(amount)
        val toUpdated = toNode.increasePopulation(amount)
        world.modifyNodes(world.nodes.updated(from, fromUpdated).updated(to, toUpdated))
        ).getOrElse(world)

    def addEdge(from: String, to: String, typology: EdgeType): World =
      val key = s"${from}_${to}_${typology.toString}"
      if world.edges.contains(key) then world
      else world.modifyEdges(world.edges + (key -> Edge(from, to, typology)))

    def removeEdge(from: String, to: String, typology: EdgeType): World =
      val key = s"${from}_${to}_${typology.toString}"
      world.modifyEdges(world.edges - key)

    def isEdgeOpen(a: String, b: String): Boolean =
      world.edges.values.exists(e =>
        ((e.nodeA == a && e.nodeB == b) || (e.nodeA == b && e.nodeB == a)) && !e.isClose
      )