package model.reaction

import model.core.SimulationState
import model.world.{EdgeType, Node, World}

trait ReactionAction:
  def apply: World => World
  def reverse: World => World

object ReactionAction:

  /** Closes all edges of a specific type connected to a node.
    * @param edgeType
    *   the type of edges to close
    * @param nodeId
    *   the identifier of the node whose edges will be closed
    */
  case class CloseEdges(edgeType: EdgeType, nodeId: String)
      extends ReactionAction:
    private def updateEdges(world: World, updateFn: model.world.Edge => model.world.Edge): World =
      val updatedEdges = world.edges.map: e =>
        if e.connects(nodeId) && e.typology == edgeType then updateFn(e)
        else e
      World(
        world.nodes,
        updatedEdges,
        world.movements
      )

    override def apply: World => World = world =>
      updateEdges(world, _.close)

    override def reverse: World => World = world =>
      updateEdges(world, _.open)

  case class CompositeAction(actions: List[ReactionAction])
      extends ReactionAction:
    override def apply: World => World =
      actions.foldLeft(identity[World] _)((acc, action) => acc.andThen(action.apply))

    override def reverse: World => World = 
      actions.foldRight(identity[World] _)((action, acc) => acc.andThen(action.reverse))
