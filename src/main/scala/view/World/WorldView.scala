package view

import controller.WorldController
import scalafx.scene.layout.Pane

class WorldView(
                  world: WorldController,
                  layout: GraphLayout,
                  nodeFactory: NodeViewFactory,
                  edgeFactory: EdgeViewFactory
               ) extends Pane:

  private val nodeViews: Seq[NodeView] =
    layout.computePositions(world.getNodes.keys.toSeq).toSeq.map { case (id, pos) =>
      nodeFactory.createNode(id, world.getNodes(id), pos)
    }

  addNodeViews()
  redrawEdges()

  private def currentEdges: Seq[Any] =
    val livePositions = nodeViews.map(n => n.id -> n.position()).toMap
    world.getEdges.toSeq.map { edge =>
      edgeFactory.createEdge(edge, livePositions)
    }

  private def addNodeViews(): Unit =
    children ++= nodeViews.flatMap(n => toJavaFXNodes(n.visuals))

  def redrawEdges(): Unit =
    children --= children.collect {
      case n: javafx.scene.shape.Line => n
    }
    children.prependAll(toJavaFXNodes(currentEdges))

  private def toJavaFXNodes(visuals: Seq[Any]): Seq[javafx.scene.Node] =
    visuals.map {
      case node: javafx.scene.Node => node
      case other => throw new IllegalArgumentException(s"Unsupported visual type: $other")
    }
