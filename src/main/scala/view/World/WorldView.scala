package view.world

import controller.WorldController
import model.core.SimulationState
import model.world.{Edge, Node, World}
import scalafx.scene.layout.Pane
import view.updatables.UpdatableView
import javafx.scene.shape.Line

class WorldView(world: World) extends Pane with UpdatableView:

  private val worldRenderer = new WorldRenderer(world, this)
  private val layout = new CircularLayout()

  private var nodeViews: Map[String, NodeView] = Map.empty
  private var edgeViews: Map[String, Line] = Map.empty

  private val positionsMap: Map[String, (Double, Double)] =
    layout.computePositions(world.nodes.keySet.toSeq)

  private val nodeLayer: NodeLayer =
    NodeLayer.fromNodes(
      nodes = world.nodes,
      layout = id => positionsMap(id),
      onMoved = () => redrawEdges(world.edges.values)
    )

  private val edgeLayer: EdgeLayer =
    EdgeLayer(
      edges = world.edges.values,
      nodePositions = nodeLayer.positions
    )

  children ++= edgeLayer.edgeLines.values.toSeq ++ nodeLayer.allVisuals

  nodeViews = nodeLayer.nodeViews
  edgeViews = edgeLayer.edgeLines

  private def redrawEdges(updatedEdges: Iterable[Edge]): Unit =
    val (newEdgeMap, toAdd, toRemove) =
      EdgeUpdater.update(
        edgeViews,
        updatedEdges,
        nodeViews.view.mapValues(nv => LivePosition(nv.position)).toMap
      )


    edgeViews = newEdgeMap
    children.removeAll(toRemove.toSeq*)
    children.addAll(toAdd.toSeq*)

  private def redrawNodes(updatedNodes: Map[String, Node]): Unit =
    val (newNodeMap, toAdd, toRemove) = NodeUpdater.update(nodeViews, updatedNodes)
    nodeViews = newNodeMap
    children.removeAll(toRemove.toSeq*)
    children.addAll(toAdd.toSeq*)

  /**
   * Updates the visual representation of the world using the given simulation state.
   *
   * This includes updating the node views, edge views, and delegating to the world renderer
   * to refresh layout and visuals.
   *
   * @param state the current simulation state with updated world data
   */
  override def update(state: SimulationState): Unit =
    redrawNodes(state.world.nodes)
    redrawEdges(state.world.edges.values)
    worldRenderer.update(state)