package dsl.builders.world

import model.world.{Edge, MovementStrategy, Node, World}

/**
 * A DSL like builder for creating a [[World]] instance.
 */
case class WorldBuilder (
                          nodes: Map[String, Node] = Map.empty,
                          edges: Map[String, Edge] = Map.empty,
                          movements: Map[MovementStrategy, Double] = Map.empty
                        ):
  /**
   * Sets the nodes for the world.
   */
  def withNodes(newNodes: Map[String, Node]): WorldBuilder =
    copy(nodes = newNodes)

  /**
   * Sets the edges for the world.
   */
  def withEdges(newEdges: Map[String, Edge]): WorldBuilder =
    copy(edges = newEdges)

  /**
   * Sets the movement strategies for the world.
   */
  def withMovements(newMovements: Map[MovementStrategy, Double]): WorldBuilder =
    copy(movements = newMovements)

  /**
   * Builds the [[World]] instance with the configured properties.
   */
  def build(): World =
    World(nodes, edges, movements)
