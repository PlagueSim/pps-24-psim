package view.world

import model.world.*

trait NodeViewFactory:
  def createNode(
                  id: String,
                  data: Node,
                  position: (Double, Double)
                ): NodeView
