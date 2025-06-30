package model.World

case class World private(
                          nodes: Map[String, Node],
                          edges: Set[Edge],
                          movements: Map[String, MovementStrategy]
                        )

object World:

  def empty: World =
    World(Map.empty, Set.empty, Map.empty)

  def apply(
             nodes: Map[String, Node],
             edges: Set[Edge],
             movements: Map[String, MovementStrategy]
           ): World =
    require(
      edges.forall(e => nodes.contains(e.nodeA) && nodes.contains(e.nodeB)),
      "Edges must connect existing nodes"
    )
    require(
      movements.keySet.subsetOf(nodes.keySet),
      "Movements must refer to existing nodes"
    )
    new World(nodes, edges, movements)