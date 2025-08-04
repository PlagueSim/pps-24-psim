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

  def createSmallWorld(): World =

    val nodes = Map(
      "VIL" -> Node.withPopulation(120).build(), // Villaggio
      "CIT" -> Node.withPopulation(95).build(), // Città
      "CAP" -> Node.withPopulation(110).build(), // Capitale
      "POR" -> Node.withPopulation(80).build(), // Porto
      "ISO" -> Node.withPopulation(50).build(), // Isola
      "MON" -> Node.withPopulation(70).build() // Montagna
    )

    val edges = Set(
      // Land connections (strade locali)
      Edge("VIL", "CIT", EdgeType.Land),
      Edge("CIT", "CAP", EdgeType.Land),
      Edge("CAP", "MON", EdgeType.Land),

      // Air connections (voli regionali)
      Edge("VIL", "ISO", EdgeType.Air),
      Edge("CAP", "ISO", EdgeType.Air),

      // Sea connections (rotte costiere)
      Edge("POR", "ISO", EdgeType.Sea),
      Edge("CIT", "POR", EdgeType.Sea),
      Edge("POR", "CAP", EdgeType.Sea)
    )

    World(
      nodes,
      edges.getMapEdges,
      Map(Static -> 0.3, GlobalLogicMovement -> 0.7)
    )

  def createLargeWorld(): World =

    val nodes = Map(
      // Nodi principali (popolazioni maggiori)
      "CHN" -> Node.withPopulation(143932776).build(), // Cina
      "IND" -> Node.withPopulation(138000385).build(), // India
      "USA" -> Node.withPopulation(33100251).build(), // Stati Uniti
      "IDN" -> Node.withPopulation(27352315).build(), // Indonesia
      "PAK" -> Node.withPopulation(22089240).build(), // Pakistan
      "BRA" -> Node.withPopulation(21255917).build(), // Brasile

      // Nodi secondari (popolazioni medie)
      "NGA" -> Node.withPopulation(20613989).build(), // Nigeria
      "BGD" -> Node.withPopulation(16468983).build(), // Bangladesh
      "RUS" -> Node.withPopulation(14593462).build(), // Russia
      "MEX" -> Node.withPopulation(12893253).build(), // Messico

      // Nodi minori (popolazioni minori)
      "JPN" -> Node.withPopulation(12647661).build(), // Giappone
      "DEU" -> Node.withPopulation(8378392).build() // Germania
    )

    val edges = Set(
      // =====================
      // CONNESSIONI TERRESTRI
      // =====================
      // Confini diretti
      Edge("CHN", "IND", EdgeType.Land),
      Edge("CHN", "PAK", EdgeType.Land),
      Edge("CHN", "RUS", EdgeType.Land),
      Edge("IND", "PAK", EdgeType.Land),
      Edge("USA", "MEX", EdgeType.Land),
      Edge("RUS", "DEU", EdgeType.Land),

      // ===================
      // CONNESSIONI AEREE
      // ===================
      // Hub principali
      Edge("USA", "CHN", EdgeType.Air),
      Edge("USA", "IND", EdgeType.Air),
      Edge("USA", "DEU", EdgeType.Air),
      Edge("DEU", "CHN", EdgeType.Air),
      Edge("DEU", "IND", EdgeType.Air),
      Edge("DEU", "JPN", EdgeType.Air),

      // Collegamenti secondari
      Edge("BRA", "IDN", EdgeType.Air),
      Edge("IDN", "JPN", EdgeType.Air),
      Edge("PAK", "RUS", EdgeType.Air),

      // ===================
      // CONNESSIONI MARITTIME
      // ===================
      // Rotte commerciali
      Edge("CHN", "USA", EdgeType.Sea),
      Edge("IND", "BRA", EdgeType.Sea),
      Edge("IDN", "BRA", EdgeType.Sea),
      Edge("NGA", "BRA", EdgeType.Sea),
      Edge("JPN", "USA", EdgeType.Sea),

      // Collegamenti regionali
      Edge("BGD", "IDN", EdgeType.Sea),
      Edge("MEX", "JPN", EdgeType.Sea),
      Edge("RUS", "JPN", EdgeType.Sea)
    )

    World(
      nodes,
      edges.getMapEdges,
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
