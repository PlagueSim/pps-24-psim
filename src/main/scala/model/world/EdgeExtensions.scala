package model.world

object EdgeExtensions:
  /*
  * EdgeExtensions provides extension methods for working with edges in the world.
  * It includes methods to access edge properties, such as typology, connectivity,
  * and edge IDs.
  * These extensions allow for more readable and concise code when dealing with edges.
  * */
  extension (entry: (String, Edge))
    def typology: EdgeType = entry._2.typology
    def isClose: Boolean = entry._2.isClose
    def close: (String, Edge) = (entry._1, entry._2.close)
    def edge: Edge = entry._2
    def id: String = entry._1
    def connects(nodeId: String): Boolean = entry._2.connects(nodeId)

  /*
  * method to get the edge ID based on the node IDs and typology.
  * The edge ID is constructed in a way that ensures consistent ordering,
  * */
  extension (edge: Edge)
    def edgeId: String =
      if edge.nodeA < edge.nodeB then s"${edge.nodeA}-${edge.nodeB}-${edge.typology}" else s"${edge.nodeB}-${edge.nodeA}-${edge.typology}"

  /*
  * Converts an iterable of edges into a map where the keys are edge IDs
  * and the values are the corresponding edges.
  * This is useful creating a World without dealing with IDs
  * */
  extension (edges: Iterable[Edge])
    def getMapEdges: Map[String, Edge] =
      edges.map(edge => edge.edgeId -> edge).toMap