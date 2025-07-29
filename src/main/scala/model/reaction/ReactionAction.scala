package model.reaction

import model.core.SimulationState
import model.world.{EdgeType, Node, World}

trait ReactionAction:
  def apply: World => World
  def reverse: World => World

object ReactionAction:

  /**
   * Closes all edges of a specific type connected to a node.
   * @param edgeType the type of edges to close
   * @param nodeId the identifier of the node whose edges will be closed
   */
  case class CloseEdges(edgeType: EdgeType, nodeId: String)
      extends ReactionAction:
    /** Updates edges connected to the node with the given function. */
    private def updateEdges(world: World, updateFn: model.world.Edge => model.world.Edge): World =
      val updatedEdges = world.edges.map {
        case (id, edge) if edge.connects(nodeId) && edge.typology == edgeType =>
          id -> updateFn(edge)
        case (id, edge) =>
          id -> edge
      }
      World(
        world.nodes,
        updatedEdges,
        world.movements
      )

    /** Closes the edges of the specified type for the node. */
    override def apply: World => World = world =>
      updateEdges(world, _.close)

    /** Reopens the edges of the specified type for the node. */
    override def reverse: World => World = world =>
      updateEdges(world, _.open)

  /**
   * Represents a sequence of actions to be applied or reversed in order.
   * @param actions the list of actions to compose
   */
  case class CompositeAction(actions: List[ReactionAction])
      extends ReactionAction:
    /** Applies all actions in sequence to the World. */
    override def apply: World => World =
      actions.foldLeft(identity[World] _)((acc, action) => acc.andThen(action.apply))

    /** Reverses all actions in reverse order on the World. */
    override def reverse: World => World =
      actions.foldRight(identity[World] _)((action, acc) => acc.andThen(action.reverse))
