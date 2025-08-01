package view.world

import javafx.scene.Node as FxNode
import javafx.scene.shape.Line
import model.world.{Edge, Node, World}
import scalafx.scene.layout.Pane

object WorldRenderer:
  /**
   * Renders the world by combining node views and edge views into a sequence of JavaFX nodes.
   * * @param nodeViews A map of node IDs to their corresponding NodeView instances.
   * * @param edgeViews A map of edge IDs to their corresponding Line instances.
   * * @param layout A CircularLayout instance that computes positions for the nodes.
   * * @return A sequence of JavaFX nodes representing the edges and positioned node visuals.
   * */
  def render(
              nodeViews: Map[String, NodeView],
              edgeViews: Map[String, Line],
              layout: CircularLayout
            ): Seq[FxNode] =
    val positions = layout.computePositions(nodeViews.keySet.toSeq)

    val positionedNodeVisuals = nodeViews.toSeq.flatMap { case (id, view) =>
      val (x, y) = positions(id)
      view.visuals.foreach(_.relocate(x, y))
      view.visuals
    }

    edgeViews.values.toSeq ++ positionedNodeVisuals
