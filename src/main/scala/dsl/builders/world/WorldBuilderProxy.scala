package dsl.builders.world

import model.world.{Edge, MovementStrategy, Node, World}

class WorldBuilderProxy
  (get: () => WorldBuilder, set: WorldBuilder => Unit)
  extends WorldBuilder:

  override def withNodes(newNodes: Map[String, Node]): WorldBuilder =
    val updated = get().withNodes(newNodes)
    set(updated)
    updated
      
  override def withEdges(newEdges: Map[String, Edge]): WorldBuilder =
    val updated = get().withEdges(newEdges)
    set(updated)
    updated
      
  override def withMovements(newMovements: Map[MovementStrategy, Double]): WorldBuilder =
    val updated = get().withMovements(newMovements)
    set(updated)
    updated
      
  override def build(): World =
    get().build()
