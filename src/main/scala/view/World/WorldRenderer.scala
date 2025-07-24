package view.world

import controller.WorldController
import javafx.scene.Node as FxNode
import javafx.scene.shape.Line
import model.core.SimulationState
import model.world.{Edge, Node, World}
import scalafx.scene.layout.Pane

class WorldRenderer(worldController: WorldController, pane: Pane):

  private var nodeLayer: NodeLayer = createNodeLayer(worldController.getNodes)
  private var edgeLayer: EdgeLayer = createEdgeLayer(worldController.getEdges, nodeLayer.positions)

  pane.children.addAll(
    (edgeLayer.edgeLines.values ++ nodeLayer.allVisuals).toSeq*
  )

  /**
   * Updates the visual representation of the world using the provided simulation state.
   *
   * This method updates the node and edge layers, recalculates positions,
   * and efficiently updates the scene by reusing visuals where possible.
   *
   * @param state the current simulation state containing the latest world data
   */
  def update(state: SimulationState): Unit =
    val newNodeLayer = createNodeLayer(state.world.nodes)
    nodeLayer.updateWith(newNodeLayer)

    val newEdgeLayer = createEdgeLayer(state.world.edges.values, newNodeLayer.positions)
    val updatedEdgeLines = newEdgeLayer.updateEdges(state.world.edges.values)

    updateScene(newNodeLayer.allVisuals, updatedEdgeLines.values.toSeq)

    nodeLayer = newNodeLayer
    edgeLayer = newEdgeLayer

  private def redraw(): Unit =
    val updatedEdgeLines = edgeLayer.updateEdges(worldController.getEdges)
    updateScene(nodeLayer.allVisuals, updatedEdgeLines.values.toSeq)

  private def updateScene(nodes: Seq[FxNode], edges: Seq[FxNode]): Unit =
    val current = pane.children.toSet
    val updated = (edges ++ nodes).toSet

    val toRemove = current -- updated
    val toAdd = updated -- current

    pane.children.removeAll(toRemove.toSeq *)

    val (toAddEdges, toAddNodes) = toAdd.partition(_.isInstanceOf[Line])
    pane.children.addAll((toAddEdges.toSeq ++ toAddNodes.toSeq) *)


  private def createNodeLayer(nodes: Map[String, Node]): NodeLayer =
    val layout = CircularLayout()
    val positionsMap = layout.computePositions(nodes.keySet.toSeq)
    NodeLayer.fromNodes(
      nodes,
      id => positionsMap(id),
      () => redraw()
    )


  private def createEdgeLayer(edges: Iterable[Edge], nodePositions: Map[String, () => (Double, Double)]): EdgeLayer =
    EdgeLayer(edges, nodePositions)
