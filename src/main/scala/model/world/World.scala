package model.world
import model.world.MovementComputation.PeopleMovement
import org.apache.commons.math3.distribution.HypergeometricDistribution

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
    WorldValidator.validateEdges(nodes, edges)
    WorldValidator.validateMovements(movements)
    new World(nodes, edges, movements)

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
    def isEdgeOpen(a: String, b: String): Boolean =
      world.edges.values.exists(e =>
        ((e.nodeA == a && e.nodeB == b) || (e.nodeA == b && e.nodeB == a)) && !e.isClose
      )
    def getAvgPopulationPerNode: Int =
      if world.nodes.isEmpty then 0
      else world.nodes.values.map(_.population).sum / world.nodes.size