package view.world

import javafx.scene.shape.Line
import scalafx.scene.paint.Color
import model.world.{Edge, EdgeType}

class DefaultEdgeViewFactory(
                              edgeStyle: Map[EdgeType, ((Double, Double), Color)]
                            ) extends EdgeViewFactory:

  override def createEdge(
                           id: String,
                           edge: Edge,
                           positions: Map[String, (Double, Double)]
                         ): EdgeView =
    val (startX, startY) = positions(edge.nodeA)
    val (endX, endY) = positions(edge.nodeB)

    val ((dx, dy), color) = edgeStyle.getOrElse(edge.typology, ((0.0, 0.0), Color.Gray))

    val line = new Line(
      startX + dx, startY + dy,
      endX + dx, endY + dy
    )
    line.setStroke(color)
    line.setStrokeWidth(2.0)

    new EdgeView(line)
