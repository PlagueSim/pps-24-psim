package view.world

import controller.WorldController
import model.core.SimulationState
import scalafx.scene.layout.Pane
import view.updatables.UpdatableView
import javafx.scene.Node as FxNode

class WorldView(
                 worldController: WorldController,
                 layout: GraphLayout,
                 nodeFactory: NodeViewFactory,
                 edgeFactory: EdgeViewFactory
               ) extends Pane with UpdatableView:

  private val nodeViews: Seq[NodeView] = buildNodeViews()
  private val nodeViewMap: Map[String, NodeView] = nodeViews.map(n => n.id -> n).toMap
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
    val livePositions = nodeViews.map(n => n.id -> n.position()).toMap
    val visuals = worldController.getEdges.toSeq.map: 
      edge => edgeFactory.createEdge(edge, livePositions)
    
    toJavaFXNodes(visuals)

  def redrawEdges(): Unit =
    children --= children.collect { case line: javafx.scene.shape.Line => line }
    children.prependAll(currentEdges())

  override def update(newState: SimulationState): Unit =
    newState.world.nodes.foreach { case (id, node) =>
      nodeViewMap.get(id).foreach { view =>
        view.labels("id").text = s"Node: $id"
        view.labels("pop").text = s"Pop: ${node.population}"
        view.labels("inf").text = s"Infected: ${node.infected}"
        view.labels("died").text = s"Died: ${node.died}"
      }
    }