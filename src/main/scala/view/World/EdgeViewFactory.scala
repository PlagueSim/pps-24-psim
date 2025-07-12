package view.world

import model.world.*

trait EdgeViewFactory:
  def createEdge(
                  edge: Edge,
                  nodePositions: Map[String, (Double, Double)]
                ): Any
