package view

import model.World.*

trait EdgeViewFactory:
  def createEdge(
                  edge: Edge,
                  nodePositions: Map[String, (Double, Double)]
                ): Any
