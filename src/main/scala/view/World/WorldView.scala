package view.world

import controller.WorldController
import model.core.SimulationState
import scalafx.scene.layout.Pane
import view.updatables.UpdatableView

class WorldView(
                  world: WorldController,
                  layout: GraphLayout,
                  nodeFactory: NodeViewFactory,
                  edgeFactory: EdgeViewFactory
               ) extends Pane with UpdatableView:

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

  override def update(newState: SimulationState): Unit =
    newState.world.nodes.foreach { case (id, node) =>
    nodeViews.find(_.id == id).foreach { view =>
      view.labelId.text = s"Node: $id"
      view.labelPop.text = s"Pop: ${node.population}"
      view.labelInf.text = s"Infected: ${node.infected}"
    }
  }


