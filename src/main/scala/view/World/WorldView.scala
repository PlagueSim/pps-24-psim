package view.world

import controller.WorldController
import model.core.SimulationState
import model.world.{Edge, Node}
import scalafx.scene.layout.Pane
import view.updatables.UpdatableView
import javafx.scene.shape.Line

class WorldView(worldController: WorldController) extends Pane with UpdatableView:

  val worldRenderer = new WorldRenderer(worldController, this)
  private val layout = new CircularLayout()

  private var nodeViews: Map[String, NodeView] = Map.empty
  private var edgeViews: Map[String, Line] = Map.empty

  private val positionsMap: Map[String, (Double, Double)] =
    layout.computePositions(worldController.getNodes.keySet.toSeq)

  private val nodeLayer: NodeLayer =
    NodeLayer.fromNodes(
      nodes = worldController.getNodes,
      layout = id => positionsMap(id),
      onMoved = () => redrawEdges(worldController.getEdges)
    )

  private val edgeLayer: EdgeLayer =
    EdgeLayer(
      edges = worldController.getEdges,
      nodePositions = nodeLayer.positions
    )

  children ++= edgeLayer.edgeLines.values.toSeq ++ nodeLayer.allVisuals

  nodeViews = nodeLayer.nodeViews
  edgeViews = edgeLayer.edgeLines

  def redrawEdges(updatedEdges: Iterable[Edge]): Unit =
    val (newEdgeMap, toAdd, toRemove) =
      EdgeUpdater.update(edgeViews, updatedEdges, nodeViews.view.mapValues(nv => () => nv.position()).toMap)

    edgeViews = newEdgeMap
    children.removeAll(toRemove.toSeq*)
    children.addAll(toAdd.toSeq*)

  private def redrawNodes(updatedNodes: Map[String, Node]): Unit =
    val (newNodeMap, toAdd, toRemove) = NodeUpdater.update(nodeViews, updatedNodes)
    nodeViews = newNodeMap
    children.removeAll(toRemove.toSeq*)
    children.addAll(toAdd.toSeq*)

  override def update(state: SimulationState): Unit =
    redrawNodes(state.world.nodes)
    redrawEdges(state.world.edges.values)
    worldRenderer.update(state)