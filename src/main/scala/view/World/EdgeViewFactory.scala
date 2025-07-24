package view.world

import model.world.*

trait EdgeViewFactory:
  def createEdge(id: String, edge: Edge, positions: Map[String, (Double, Double)]): EdgeView

