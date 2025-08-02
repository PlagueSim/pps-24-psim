package view.world

import model.world.*

trait NodeViewFactory:
  /* Creates a NodeView instance for a node with the given ID, model data, and position. */
  def createNode(
                  id: String,
                  data: Node,
                  position: (Double, Double)
                ): NodeView
