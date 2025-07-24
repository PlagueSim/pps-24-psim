package view.world

import model.world.{Edge, EdgeType}
import javafx.scene.shape.Line
import scalafx.scene.paint.Color

class EdgeLayer(
                 edges: Iterable[Edge],
                 nodePositions: Map[String, () => (Double, Double)]
               ):

  import EdgeLayer.*

  private def edgeId(a: String, b: String): String =
    if a < b then s"$a-$b" else s"$b-$a"

  val edgeLines: Map[String, Line] = edges.map { edge =>
    val id = edgeId(edge.nodeA, edge.nodeB)
    id -> createEdgeLine(edge, nodePositions)
  }.toMap

  def updateEdges(updatedEdges: Iterable[Edge]): Map[String, Line] =
    updatedEdges.map { edge =>
      val id = edgeId(edge.nodeA, edge.nodeB)
      val line = edgeLines.get(id) match
        case Some(existing) =>
          updateLine(existing, edge, nodePositions)
          existing
        case None =>
          createEdgeLine(edge, nodePositions)
      id -> line
    }.toMap

object EdgeLayer:

  def createEdgeLine(edge: Edge, nodePositions: Map[String, () => (Double, Double)]): Line =
    val (startX, startY) = nodePositions(edge.nodeA)()
    val (endX, endY) = nodePositions(edge.nodeB)()
    val line = new Line(
      startX, startY,
      endX, endY
    )
    line.setStroke(edgeColor(edge.typology, edge.isClose))
    line

  def updateLine(line: Line, edge: Edge, nodePositions: Map[String, () => (Double, Double)]): Unit =
    val (startX, startY) = nodePositions(edge.nodeA)()
    val (endX, endY) = nodePositions(edge.nodeB)()
    line.setStartX(startX)
    line.setStartY(startY)
    line.setEndX(endX)
    line.setEndY(endY)
    line.setStroke(edgeColor(edge.typology, edge.isClose))

  def edgeColor(edgeType: EdgeType, isClose: Boolean): Color =
    if isClose then Color.Gray
    else
      edgeType match
        case EdgeType.Land => Color.Green
        case EdgeType.Sea  => Color.Blue
        case EdgeType.Air  => Color.Red
