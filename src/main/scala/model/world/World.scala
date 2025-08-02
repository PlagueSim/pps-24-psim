package model.world
import model.world.MovementComputation.PeopleMovement
import Types.*
case class World private (
                           nodes: Map[NodeId, Node],
                           edges: Map[EdgeId, Edge],
                           movements: Map[MovementStrategy, Percentage]
                         ):
  /** @return a new World instance with updated nodes */
  def modifyNodes(newNodes: Map[NodeId, Node]): World = copy(nodes = newNodes)

  /** @return a new World instance with updated edges */
  def modifyEdges(newEdges: Map[NodeId, Edge]): World = copy(edges = newEdges)

  /** @return a new World instance with updated movement strategies */
  def modifyMovements(newMovements: Map[MovementStrategy, Percentage]): World = copy(movements = newMovements)

  /** @return all edges in the world */
  def getEdges: Iterable[Edge] =
    edges.values

  /**
   * Returns a set of all nodes in the world.
   * @param nodeId the ID of the node to find neighbors for
   *
   * @return a set of node IDs
   * */
  def neighbors(nodeId: NodeId): Set[NodeId] =
    edges.values.collect {
      case e if e.nodeA == nodeId => e.nodeB
      case e if e.nodeB == nodeId => e.nodeA
    }.toSet

  /**
   * @param nodeA the ID of the first node
   * @param nodeB the ID of the second node
   *
   * @return true if there is at least one edge connecting the two given nodes */
  def areConnected(nodeA: NodeId, nodeB: NodeId): Boolean =
    edges.values.exists(e =>
      (e.nodeA == nodeA && e.nodeB == nodeB) ||
        (e.nodeA == nodeB && e.nodeB == nodeA)
    )

object World:
  /* Creates a World instance after validating edges and movement strategies */
  def apply(
             nodes: Map[NodeId, Node],
             edges: Map[EdgeId, Edge],
             movements: Map[MovementStrategy, Percentage]
           ): World =
    WorldValidator.validateEdges(nodes, edges)
    WorldValidator.validateMovements(movements)
    new World(nodes, edges, movements)




  extension (world: World)
    /**
     * Checks if there is an open edge between two nodes.
     * @param a the ID of the first node
     * @param b the ID of the second node
     *
     * @return true if there is an open edge between the two nodes, false otherwise
     * */
    def isEdgeOpen(a: NodeId, b: NodeId): Boolean =
      world.edges.values.exists(e =>
        ((e.nodeA == a && e.nodeB == b) || (e.nodeA == b && e.nodeB == a)) && !e.isClose
      )

    /**
     * @return the average population per node, or 0 if there are no nodes
     */
    def getAvgPopulationPerNode: Int =
      if world.nodes.isEmpty then 0
      else world.nodes.values.map(_.population).sum / world.nodes.size