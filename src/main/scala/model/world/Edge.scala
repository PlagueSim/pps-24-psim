package model.world

import Types.*
/* Describes the type of connection between nodes.*/
enum EdgeType:
  case Air, Sea, Land

/* Represents an undirected connection of a specific typology between two nodes */
case class Edge private (
                          nodeA: NodeId,
                          nodeB: NodeId,
                          typology: EdgeType,
                          isClose: Boolean = false,
                        ):

  /**
   * Returns a unique identifier for the edge based on its nodes and typology.
   * @param nodeId
   * 
   * @return A unique string identifier for the edge.
   */
  def connects(nodeId: NodeId): Boolean =
    nodeId == nodeA || nodeId == nodeB

  /**
   * Returns the other node ID if this edge connects to the given node ID.
   * If the edge does not connect to the given node ID, returns None.
   * @param nodeId The ID of the node to check.
   * 
   * @return An Option containing the other node ID if it exists, otherwise None.
   */
  def other(nodeId: NodeId): Option[NodeId] =
    if nodeId == nodeA then Some(nodeB)
    else if nodeId == nodeB then Some(nodeA)
    else None

  /** Marks this edge as closed. */
  def close: Edge =
    copy(isClose = true)

  /** Marks this edge as open.*/
  def open: Edge =
    copy(isClose = false)


object Edge:
  /** Factory method that ensures consistent ordering for undirected edges.
   * @param a The ID of the first node.
   * @param b The ID of the second node.
   * @param typology The type of connection between the nodes.
   * 
   * @return An Edge instance with nodes ordered lexicographically.
   * */
  def apply(a: NodeId, b: NodeId, typology: EdgeType): Edge =
    if a <= b then new Edge(a, b, typology)
    else new Edge(b, a, typology)