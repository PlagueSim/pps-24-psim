package model.world

/* Describes the type of connection between nodes.*/
enum EdgeType:
  case Air, Sea, Land

/* Represents an undirected connection of a specific typology between two nodes */
case class Edge private (
                          nodeA: String,
                          nodeB: String,
                          typology: EdgeType,
                          isClose: Boolean = false,
                        ):

  /* Checks if this edge connects to the given node ID. */
  def connects(nodeId: String): Boolean =
    nodeId == nodeA || nodeId == nodeB

  /*
   * Returns the other node ID if this edge connects to the given node ID.
   * If the edge does not connect to the given node ID, returns None.
   */
  def other(nodeId: String): Option[String] =
    if nodeId == nodeA then Some(nodeB)
    else if nodeId == nodeB then Some(nodeA)
    else None

  /* Marks this edge as closed. */
  def close: Edge =
    copy(isClose = true)

  /*Marks this edge as open.*/
  def open: Edge =
    copy(isClose = false)


object Edge:
  /* Factory method that ensures consistent ordering for undirected edges. */
  def apply(a: String, b: String, typology: EdgeType): Edge =
    if a <= b then new Edge(a, b, typology)
    else new Edge(b, a, typology)
    
    
