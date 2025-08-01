package model.world
import model.world.MovementComputation.PeopleMovement
import org.apache.commons.math3.distribution.HypergeometricDistribution

case class World private (
                           nodes: Map[String, Node],
                           edges: Map[String, Edge],
                           movements: Map[MovementStrategy, Double]
                         ):
  /** @return a new World instance with updated nodes */
  def modifyNodes(newNodes: Map[String, Node]): World = copy(nodes = newNodes)

  /** @return a new World instance with updated edges */
  def modifyEdges(newEdges: Map[String, Edge]): World = copy(edges = newEdges)

  /** @return a new World instance with updated movement strategies */
  def modifyMovements(newMovements: Map[MovementStrategy, Double]): World = copy(movements = newMovements)

  /** @return all edges in the world */
  def getEdges: Iterable[Edge] =
    edges.values

  /** 
   * Returns a set of all nodes in the world.
   * @param nodeId the ID of the node to find neighbors for
   *               
   * @return a set of node IDs
   * */
  def neighbors(nodeId: String): Set[String] =
    edges.values.collect {
      case e if e.nodeA == nodeId => e.nodeB
      case e if e.nodeB == nodeId => e.nodeA
    }.toSet

  /**
   * @param nodeA the ID of the first node
   * @param nodeB the ID of the second node
   *              
   * @return true if there is at least one edge connecting the two given nodes */
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
    WorldValidator.validateEdges(nodes, edges)
    WorldValidator.validateMovements(movements)
    new World(nodes, edges, movements)

  /**
   * Applies a list of movements to the world.
   * This method updates the population and infection counts of nodes based on the movements.
   * @param world The current state of the world containing nodes and edges.
   * @param movements An iterable of PeopleMovement instances representing the movements to apply.
   * 
   * @return A new World instance with updated nodes after applying the movements.
   * */
  def applyMovements(world: World, movements: Iterable[PeopleMovement]): World = {
    val updatedNodes = movements.foldLeft(world.nodes):
      case (nodesAcc, move) => updateNodesWithMovement(nodesAcc, move)
    world.copy(nodes = updatedNodes)
  }

  private def updateNodesWithMovement(
                                       nodes: Map[String, Node],
                                       movement: PeopleMovement
                                     ): Map[String, Node] =
    val PeopleMovement(from, to, amount) = movement
    
    val fromNode = nodes(from)
    if fromNode.population <= 0 then return nodes
    
    val infectedMoving = sampleInfected(fromNode, amount)
    
    val updatedFrom = fromNode
      .decreasePopulation(amount)
      .decreaseInfection(infectedMoving)
    
    val updatedTo = nodes(to)
      .increasePopulation(amount)
      .increaseInfection(infectedMoving)
    
    nodes.updated(from, updatedFrom)
      .updated(to, updatedTo)

  private def sampleInfected(node: Node, amount: Int): Int =
    val hgd = new HypergeometricDistribution(
      node.population,
      node.infected,
      amount
    )
    hgd.sample()
  

  extension (world: World)
    /**
     * Checks if there is an open edge between two nodes.
     * @param a the ID of the first node
     * @param b the ID of the second node
     * 
     * @return true if there is an open edge between the two nodes, false otherwise
     * */
    def isEdgeOpen(a: String, b: String): Boolean =
      world.edges.values.exists(e =>
        ((e.nodeA == a && e.nodeB == b) || (e.nodeA == b && e.nodeB == a)) && !e.isClose
      )

    /**
     * @return the average population per node, or 0 if there are no nodes
     */
    def getAvgPopulationPerNode: Int =
      if world.nodes.isEmpty then 0
      else world.nodes.values.map(_.population).sum / world.nodes.size