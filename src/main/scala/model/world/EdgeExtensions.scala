package model.world

object EdgeExtensions:
  extension (entry: (String, Edge))
    def typology: EdgeType = entry._2.typology
    def isClose: Boolean = entry._2.isClose
    def close: (String, Edge) = (entry._1, entry._2.close)
    def edge: Edge = entry._2
    def id: String = entry._1