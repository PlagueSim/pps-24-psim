package view

import scalafx.scene.shape.Line
import scalafx.scene.paint.Color
import model.world.*

class DefaultEdgeViewFactory(
                              edgeStyle: Map[EdgeType, ((Int, Int), Color)]
                            ) extends EdgeViewFactory:

  override def createEdge(edge: Edge, nodePositions: Map[String, (Double, Double)]): Any =
    val ((dx, dy), color) = edgeStyle(edge.typology)
    val (x1, y1) = nodePositions(edge.nodeA)
    val (x2, y2) = nodePositions(edge.nodeB)

    (
      new Line:
        startX = x1 + dx
        startY = y1 + dy
        endX = x2 + dx
        endY = y2 + dy
        stroke = color
        strokeWidth = 2
      ).delegate
