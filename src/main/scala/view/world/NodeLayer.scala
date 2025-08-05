package view.world

import javafx.scene.Node as FxNode
import model.world.Node
import scalafx.scene.text.Text
import model.world.Types.*
case class NodeLayer(
                      nodeViews: Map[NodeId, NodeView],
                      positions:  Map[NodeId, LivePosition],
                      allVisuals: Seq[FxNode]
                    ):
  /**
   *  Updates the label texts of the existing node views using data from a new NodeLayer.
   * @param newLayer The new NodeLayer containing updated node views.
   * This method iterates through the node views in the new layer and updates the labels
   *  */
  def updateWith(newLayer: NodeLayer): Unit =
    for ((id, newView) <- newLayer.nodeViews) do
      nodeViews.get(id).foreach { oldView =>
        oldView.updateLabels(newView)
      }

object NodeLayer:
  /** Creates a NodeLayer from the given nodes and layout strategy.
   * 
   * @param nodes A map of node IDs to Node instances.
   * @param layout A function that takes a node ID and returns its position as a tuple (x, y).
   * @param onMoved A callback function to be called when a node is moved.
   * 
   * @return A NodeLayer containing NodeViews for each node, their positions, and all visuals.
   * */
  def fromNodes(
                 nodes: Map[NodeId, Node],
                 layout: String => (PosX, PosY),
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
    
  /** Creates a NodeView for a given node with a specific position and callback for movement.
   * 
   * @param id The unique identifier for the node.
   * @param data The Node instance containing the data for the node.
   * @param position The initial position of the node as a tuple (x, y).
   * @param onMoved A callback function to be called when the node is moved.
   *   
   * @return A NodeView instance representing the node.
   * */
  def createNode(
                 id: NodeId,
                 data: Node,
                 position: (PosX, PosY),
                 onMoved: () => Unit
               ): NodeView =
    val factory = new DefaultNodeViewFactory(onMoved)
    factory.createNode(id, data, position)
