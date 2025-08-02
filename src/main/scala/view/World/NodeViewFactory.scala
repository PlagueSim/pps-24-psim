package view.world

import model.world.*
import model.world.Types.*
trait NodeViewFactory:
  /* Creates a NodeView instance for a node with the given ID, model data, and position. */
  def createNode(
                  id: NodeId,
                  data: Node,
                  position: (PosX, PosY)
                ): NodeView
