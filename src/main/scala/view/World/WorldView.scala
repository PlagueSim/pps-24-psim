package view.world

import controller.WorldController
import model.core.SimulationState
import scalafx.scene.layout.Pane
import view.updatables.UpdatableView
import javafx.scene.Node as FxNode
import model.world.Edge

class WorldView(
                 worldController: WorldController,
                 layout: GraphLayout,
                 nodeFactory: NodeViewFactory,
                 edgeFactory: EdgeViewFactory
               ) extends Pane with UpdatableView:

  private val nodeViews: Seq[NodeView] = buildNodeViews()
  private var nodeViewMap: Map[String, NodeView] = nodeViews.map(n => n.id -> n).toMap
  renderInitialView()

  private def buildNodeViews(): Seq[NodeView] =
    val positions = layout.computePositions(worldController.getNodes.keys.toSeq)
    for (id, pos) <- positions.toSeq yield
      nodeFactory.createNode(id, worldController.getNodes(id), pos)

  private def renderInitialView(): Unit =
    children ++= toJavaFXNodes(nodeViews.flatMap(_.visuals))
    redrawEdges()

  private def toJavaFXNodes(visuals: Seq[Any]): Seq[FxNode] =
    visuals.collect {
      case fx: FxNode => fx
    }

  private def currentEdges(): Seq[FxNode] =
    val liveNodeIds = nodeViewMap.keySet
    val livePositions = computeLiveNodePositions(liveNodeIds)
    val visibleEdges = filterValidEdges(liveNodeIds)

    val edgeVisuals = visibleEdges.map(edge => edgeFactory.createEdge(edge, livePositions))
    toJavaFXNodes(edgeVisuals)

  private def computeLiveNodePositions(liveNodeIds: Set[String]): Map[String, (Double, Double)] =
    nodeViews
      .filter(view => liveNodeIds.contains(view.id))
      .map(view => view.id -> view.position())
      .toMap

  private def filterValidEdges(liveNodeIds: Set[String]): Seq[Edge] =
    worldController.getEdges.toSeq
      .filter(edge => liveNodeIds.contains(edge.nodeA) && liveNodeIds.contains(edge.nodeB))

  def redrawEdges(): Unit =
    children --= children.collect { case line: javafx.scene.shape.Line => line }
    children.prependAll(currentEdges())

  override def update(newState: SimulationState): Unit =
    removeOrphanedNodes(newState)
    redrawEdges()
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

  private def updateNodeView(view: NodeView, id: String, node: model.world.Node): Unit =
    view.labels("id").text = s"Node: $id"
    view.labels("pop").text = s"Pop: ${node.population}"
    view.labels("inf").text = s"Infected: ${node.infected}"
    view.labels("died").text = s"Died: ${node.died}"




