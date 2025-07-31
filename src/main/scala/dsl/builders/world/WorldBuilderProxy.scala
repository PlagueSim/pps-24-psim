package dsl.builders.world

import model.world.{Edge, MovementStrategy, Node, World}

/** A proxy for the [[WorldBuilder]] that allows mutable updates within a DSL
  * context.
  */
class WorldBuilderProxy(get: () => WorldBuilder, set: WorldBuilder => Unit)
    extends WorldBuilder:

  /** Sets the nodes for the world and updates the underlying builder.
    */
  override def withNodes(newNodes: Map[String, Node]): WorldBuilder =
    val updated = get().withNodes(newNodes)
    set(updated)
    updated

  /** Sets the edges for the world and updates the underlying builder.
    */
  override def withEdges(newEdges: Map[String, Edge]): WorldBuilder =
    val updated = get().withEdges(newEdges)
    set(updated)
    updated

  /** Sets the movement strategies for the world and updates the underlying
    * builder.
    */
  override def withMovements(
      newMovements: Map[MovementStrategy, Double]
  ): WorldBuilder =
    val updated = get().withMovements(newMovements)
    set(updated)
    updated

  /** Builds the [[World]] instance using the current state of the underlying
    * builder.
    */
  override def build(): World =
    get().build()
