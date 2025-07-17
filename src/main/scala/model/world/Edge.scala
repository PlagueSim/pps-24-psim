package model.world

/**
 * Describes the type of connection between nodes.
 */
enum EdgeType:
  case Air, Sea, Land

/**
 * Represents an undirected connection of a specific typology between two nodes.
 *
 * @param nodeA ID of one node
 * @param nodeB ID of the other node
 * @param typology Type of the connection (Air, Sea, Land)
 * @param isClose Indicates if the edge is closed (default = false)
 */
case class Edge private (
                          nodeA: String,
                          nodeB: String,
                          typology: EdgeType,
                          isClose: Boolean = false,
                        ):

  /**
   * Checks if this edge connects to the given node ID.
   *
   * @param nodeId ID of the node to check
   * @return true if this edge connects to the given node ID, false otherwise
   */
  def connects(nodeId: String): Boolean =
    nodeId == nodeA || nodeId == nodeB

  /**
   * Returns the other node ID if this edge connects to the given node ID.
   * If the edge does not connect to the given node ID, returns None.
   *
   * @param nodeId
   * @return Option with the other node ID
   */
  def other(nodeId: String): Option[String] =
    if nodeId == nodeA then Some(nodeB)
    else if nodeId == nodeB then Some(nodeA)
    else None
    
  def close: Edge =
    copy(isClose = true)
    
  def open: Edge =
    copy(isClose = false)


object Edge:
  /**
   * Factory method that ensures consistent ordering for undirected edges.
   *
   * @param a one node ID
   * @param b another node ID
   * @param typology Type of the connection
   * @return an Edge with nodeA <= nodeB
   */
  def apply(a: String, b: String, typology: EdgeType): Edge =
    if a <= b then new Edge(a, b, typology)
    else new Edge(b, a, typology)
