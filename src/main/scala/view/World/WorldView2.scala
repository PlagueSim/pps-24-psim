package view

import scalafx.scene.layout.Pane
import model.World.*

class WorldView2(
                 world: World,
                 layout: GraphLayout,
                 nodeFactory: NodeViewFactory,
                 edgeFactory: EdgeViewFactory
               ) extends Pane:

  // Create NodeViews
  private val nodeViews: Seq[NodeView] =
    layout.computePositions(world.nodes.keys.toSeq).toSeq.map { case (id, pos) =>
      nodeFactory.createNode(id, world.nodes(id), pos)
    }

  // Add initial visuals
  addNodeViews()
  redrawEdges()

  // Build edges using live positions from nodeViews
  private def currentEdges: Seq[Any] =
    val livePositions = nodeViews.map(n => n.id -> n.position()).toMap
    world.edges.toSeq.map { edge =>
      edgeFactory.createEdge(edge, livePositions)
    }

  private def addNodeViews(): Unit =
    children ++= nodeViews.flatMap(n => toJavaFXNodes(n.visuals))

  /** Redraw all edges with live positions */
  def redrawEdges(): Unit =
    // Remove all old edges
    children --= children.collect {
      case n: javafx.scene.shape.Line => n
    }
    // Add new edges
    children.prependAll(toJavaFXNodes(currentEdges))

  private def toJavaFXNodes(visuals: Seq[Any]): Seq[javafx.scene.Node] =
    visuals.map {
      case node: javafx.scene.Node => node
      case other => throw new IllegalArgumentException(s"Unsupported visual type: $other")
    }
