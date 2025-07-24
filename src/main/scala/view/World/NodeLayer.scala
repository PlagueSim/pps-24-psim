package view.world

import javafx.scene.Node as FxNode
import model.world.Node
import scalafx.scene.text.Text

case class NodeLayer(
                      nodeViews: Map[String, NodeView],
                      positions:  Map[String, LivePosition],
                      allVisuals: Seq[FxNode]
                    ):
  /* Updates the label texts of the existing node views using data from a new NodeLayer.*/
  def updateWith(newLayer: NodeLayer): Unit =
    for ((id, newView) <- newLayer.nodeViews) do
      nodeViews.get(id).foreach { oldView =>
        oldView.updateLabels(newView)
      }

object NodeLayer:
  /* Creates a NodeLayer from the given nodes and layout strategy.*/
  def fromNodes(
                 nodes: Map[String, Node],
                 layout: String => (Double, Double),
                 onMoved: () => Unit
               ): NodeLayer =
    val factory = new DefaultNodeViewFactory(onMoved)

    val nodeViews: Map[String, NodeView] = nodes.map { case (id, node) =>
      val position = layout(id)
      id -> factory.createNode(id, node, position)
    }

    val positions: Map[String, LivePosition] =
      nodeViews.view.mapValues(nv => LivePosition(nv.position)).toMap

    val visuals: Seq[FxNode] =
      nodeViews.values.flatMap(_.visuals).toSeq

    NodeLayer(nodeViews, positions, visuals)
