package model.World

case class World private(
                          nodes: Map[String, Node],
                          edges: Set[Edge],
                          movements: Map[String, MovementStrategy]
                        )

object World:
  def empty: World =
    World(Map.empty, Set.empty, Map.empty)