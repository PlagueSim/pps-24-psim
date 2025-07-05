package view

import model.World.*

trait NodeViewFactory:
  def createNode(
                  id: String,
                  data: Node,
                  position: (Double, Double)
                ): NodeView
