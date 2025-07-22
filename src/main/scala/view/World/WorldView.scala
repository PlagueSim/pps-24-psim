package view.world

import controller.WorldController
import model.core.SimulationState
import scalafx.scene.layout.Pane
import view.updatables.UpdatableView
import javafx.scene.Node as FxNode
import model.world.{Edge, Node, World}

/**
 * WorldView renders the node and edge views for a world simulation.
 */
class WorldView(
                 worldController: WorldController,
                 layout: GraphLayout,
                 nodeFactory: NodeViewFactory,
                 edgeFactory: EdgeViewFactory
               ) extends Pane with UpdatableView:

  private val nodeViews: Seq[NodeView] = buildNodeViews()
  private var nodeViewMap: Map[String, NodeView] = nodeViews.map(n => n.id -> n).toMap
  private var edgeViewMap: Map[String, EdgeView] = Map.empty

  renderInitialView()

  private def buildNodeViews(): Seq[NodeView] =
    val positions = layout.computePositions(worldController.getNodes.keys.toSeq)
    for (id, pos) <- positions.toSeq yield
      nodeFactory.createNode(id, worldController.getNodes(id), pos)

  private def renderInitialView(): Unit =
    children ++= toJavaFXNodes(nodeViews.flatMap(_.visuals))
    redrawEdges()

  private def toJavaFXNodes(visuals: Seq[Any]): Seq[FxNode] =
    visuals.collect { case fx: FxNode => fx }

  private def edgeId(a: String, b: String): String =
    if a < b then s"$a-$b" else s"$b-$a"

  private def computeLiveNodePositions(liveNodeIds: Set[String]): Map[String, (Double, Double)] =
    nodeViews
      .filter(view => liveNodeIds.contains(view.id))
      .map(view => view.id -> view.position())
      .toMap

  private def filterValidEdges(liveNodeIds: Set[String]): Seq[Edge] =
    worldController.getEdges.toSeq
      .filter(edge => liveNodeIds.contains(edge.nodeA) && liveNodeIds.contains(edge.nodeB))

  /** Redraws all edges based on the current world from the controller */
  def redrawEdges(): Unit =
    val world = worldController.getWorld
    val liveNodeIds = nodeViewMap.keySet
    val livePositions = computeLiveNodePositions(liveNodeIds)
    val visibleEdges = world.edges.values.filter(e =>
      liveNodeIds.contains(e.nodeA) && liveNodeIds.contains(e.nodeB)
    )

  def redrawEdges(simulationState: SimulationState): Unit =
    val world = simulationState.world
    val liveNodeIds = nodeViewMap.keySet
    val livePositions = computeLiveNodePositions(liveNodeIds)
    val visibleEdges = world.edges.values.filter(e =>
      liveNodeIds.contains(e.nodeA) && liveNodeIds.contains(e.nodeB)
    )





    val updatedEdgeMap = visibleEdges.map { edge =>
      val id = edgeId(edge.nodeA, edge.nodeB)
      val edgeView = edgeViewMap.get(id) match
        case Some(existing) =>
          existing.updateLine(livePositions(edge.nodeA), livePositions(edge.nodeB))
          existing
        case None =>
          edgeFactory.createEdge(id, edge, livePositions)

      if edge.isClose then
        edgeView.setColor(scalafx.scene.paint.Color.Gray)

      id -> edgeView
    }.toMap

    edgeViewMap = updatedEdgeMap
    val edgeLines = updatedEdgeMap.values.map(_.getLine)

    children --= children.collect { case l: javafx.scene.shape.Line => l }
    children.prependAll(edgeLines)

  override def update(newState: SimulationState): Unit =
    removeOrphanedNodes(newState)
    redrawEdges(newState)
    updateExistingNodes(newState)

  private def removeOrphanedNodes(newState: SimulationState): Unit =
    val currentNodeIds = nodeViewMap.keySet
    val updatedNodeIds = newState.world.nodes.keySet
    val orphanedNodeIds = currentNodeIds -- updatedNodeIds

    orphanedNodeIds.foreach { id =>
      val visualsToRemove = toJavaFXNodes(nodeViewMap(id).visuals)
      children --= visualsToRemove
      nodeViewMap -= id
    }

  private def updateExistingNodes(newState: SimulationState): Unit =
    newState.world.nodes.foreach { case (id, node) =>
      nodeViewMap.get(id).foreach { view =>
        updateNodeView(view, id, node)
      }
    }

  private def updateNodeView(view: NodeView, id: String, node: Node): Unit =
    view.labels("id").text = s"Node: $id"
    view.labels("pop").text = s"Pop: ${node.population}"
    view.labels("inf").text = s"Infected: ${node.infected}"
    view.labels("died").text = s"Died: ${node.died}"
