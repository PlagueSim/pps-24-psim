package model.world

object EdgeExtensions:
  extension (entry: (String, Edge))
    def typology: EdgeType = entry._2.typology
    def isClose: Boolean = entry._2.isClose
    def close: (String, Edge) = (entry._1, entry._2.close)
    def edge: Edge = entry._2
    def id: String = entry._1
    def connects(nodeId: String): Boolean = entry._2.connects(nodeId)

  extension (edge: Edge)
    def edgeId: String =
      if edge.nodeA < edge.nodeB then s"${edge.nodeA}-${edge.nodeB}-${edge.typology}" else s"${edge.nodeB}-${edge.nodeA}-${edge.typology}"