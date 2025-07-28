package dsl.builders.world

import model.world.{Edge, MovementStrategy, Node, World}

case class WorldBuilder (
                          nodes: Map[String, Node] = Map.empty,
                          edges: Map[String, Edge] = Map.empty,
                          movements: Map[MovementStrategy, Double] = Map.empty
                        ):
  def withNodes(newNodes: Map[String, Node]): WorldBuilder =
    copy(nodes = newNodes)
    
  def withEdges(newEdges: Map[String, Edge]): WorldBuilder =
    copy(edges = newEdges)
    
  def withMovements(newMovements: Map[MovementStrategy, Double]): WorldBuilder =
    copy(movements = newMovements)

  def build(): World =
    World(nodes, edges, movements)
      