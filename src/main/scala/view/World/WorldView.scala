package view.world

import model.core.SimulationState
import model.world.{Edge, Node, World}
import scalafx.scene.layout.Pane
import view.event.ViewEvent
import view.updatables.UpdatableView
import javafx.scene.shape.Line
import scalafx.scene.Node as NodeVisual

class WorldView extends Pane with UpdatableView with VisualView:

  private var eventHandler: ViewEvent => Unit = _ => ()
  private var nodeViews: Map[String, NodeView] = Map.empty
  private var edgeViews: Map[String, Line] = Map.empty

  private val layout: CircularLayout = CircularLayout()
  private var worldRenderer: Option[WorldRenderer] = None
  private var currentWorld: Option[World] = None

  override def setEventHandler(handler: ViewEvent => Unit): Unit =
    this.eventHandler = handler

  override def render(world: World): Unit =
    this.currentWorld = Some(world)
    this.worldRenderer = Some(new WorldRenderer(world, this))

    val positionsMap = layout.computePositions(world.nodes.keySet.toSeq)

    val nodeLayer = NodeLayer.fromNodes(
      nodes = world.nodes,
      layout = id => positionsMap(id),
      onMoved = () => redrawEdges(world.edges.values)
    )

    val edgeLayer = EdgeLayer(
      edges = world.edges.values,
      nodePositions = nodeLayer.positions
    )

    nodeViews = nodeLayer.nodeViews
    edgeViews = edgeLayer.edgeLines

    children.setAll(edgeLayer.edgeLines.values.toSeq ++ nodeLayer.allVisuals: _*)

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

  private def update(world: World): Unit =
    redrawNodes(world.nodes)
    redrawEdges(world.edges.values)
    worldRenderer.foreach(_.update(world))

  override def update(newState: SimulationState): Unit =
    update(newState.world)

  override def handleEvent(event: ViewEvent): Unit =
    eventHandler(event)

  override def root: NodeVisual = this
