package model.world
import model.world.EdgeExtensions.*
object WorldFactory:

  /*
   * Creates a mock World instance with 15 nodes and predefined edges.
   * Some nodes are initialized with infection and death values.
   * A mix of edge types (Land, Sea, Air) is used for connections.
   */
  def createWorldWithTwoNodesAndTwoEdges(): World =
    val node = Node.withPopulation(100).withInfected(1).build()
    val world = World(
      Map("A" -> node, "B" -> node),
      List(Edge("A", "B", EdgeType.Land), Edge("A", "B", EdgeType.Sea)).getMapEdges,
      Map(GlobalLogicMovement -> 1.0)
    )
    world

  /*
  * Creates an initial World instance with 15 nodes and a predefined set of edges.
   * Each node has a population of 100,000,000 plus its index, with no initial infections or deaths.
   * The edges connect the nodes in a specific pattern, including various types (Land, Sea, Air).
   * Movement strategies are defined with specific probabilities for static and global logic movements.
  * */
  def createInitialWorld(): World =
    val nodes =
      (1 to 15).map { i =>
        val id = ('A' + (i - 1)).toChar.toString
        id -> Node.withPopulation(100000000 + i).withDied(0).withInfected(0).build()
      }.toMap

    val edgeSet: Set[Edge] = Set(
      Edge("A", "C", EdgeType.Land),
      Edge("A", "C", EdgeType.Air),
      Edge("A", "B", EdgeType.Land),
      Edge("B", "C", EdgeType.Sea),
      Edge("C", "D", EdgeType.Air),
      Edge("D", "E", EdgeType.Land),
      Edge("E", "F", EdgeType.Sea),
      Edge("F", "G", EdgeType.Air),
      Edge("G", "H", EdgeType.Land),
      Edge("H", "I", EdgeType.Sea),
      Edge("I", "J", EdgeType.Air),
      Edge("J", "K", EdgeType.Land),
      Edge("K", "L", EdgeType.Sea),
      Edge("L", "M", EdgeType.Air),
      Edge("M", "N", EdgeType.Land),
      Edge("N", "O", EdgeType.Sea),
      Edge("O", "A", EdgeType.Air),
      Edge("A", "H", EdgeType.Sea),
      Edge("C", "I", EdgeType.Land),
      Edge("D", "K", EdgeType.Air),
      Edge("F", "M", EdgeType.Sea),
      Edge("G", "O", EdgeType.Land)
    )

    World(
      nodes,
      edgeSet.getMapEdges,
      Map(Static -> 0.3, GlobalLogicMovement -> 0.7)
    )

  /**
   * Creates a mock World instance with 15 nodes, where the last node is infected.
   */
  def createWorldWithInfected(): World =
    val initialWorld = createInitialWorld()
    val infectedNode = initialWorld.nodes.last
    val updatedNodes: Map[Types.NodeId, Node] = initialWorld.nodes.map((id, node) => (id, node) match
      case (id, node) if id == infectedNode._1 =>
        id -> node.increaseInfection(1)
      case other => other
    )
    initialWorld.modifyNodes(updatedNodes)
