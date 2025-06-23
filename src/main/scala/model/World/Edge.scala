package model.World

/**
 * Represents an undirected connection between two nodes.
 *
 * @param nodeA ID of one node
 * @param nodeB ID of the other node
 * @param weight optional weight of the connection (default = 1.0)
 */
case class Edge private (
                          nodeA: String,
                          nodeB: String,
                          weight: Double = 1.0
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
   * @return
   */
  def other(nodeId: String): Option[String] =
    if nodeId == nodeA then Some(nodeB)
    else if nodeId == nodeB then Some(nodeA)
    else None


object Edge:
  /**
   * Factory method that ensures consistent ordering for undirected edges.
   *
   * @param a one node ID
   * @param b another node ID
   * @param weight optional weight (default = 1.0)
   * @return an Edge with nodeA <= nodeB
   */
  def apply(a: String, b: String, weight: Double = 1.0): Edge =
    if a <= b then new Edge(a, b, weight)
    else new Edge(b, a, weight)
