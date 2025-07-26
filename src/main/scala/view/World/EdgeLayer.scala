package view.world

import model.world.{Edge, EdgeType}
import model.world.EdgeExtensions.*
import javafx.scene.shape.Line
import scalafx.scene.paint.Color

/*
 * EdgeLayer manages the visual representation of edges in the simulation.
 * It handles creation, storage, and updates of JavaFX Line objects.
 */
class EdgeLayer(
                 edges: Iterable[Edge],
                 nodePositions: Map[String, LivePosition]
               ):

  import EdgeLayer.*

  /* Map of edge IDs to their corresponding JavaFX Line visuals. */
  val edgeLines: Map[String, Line] = edges.flatMap { edge =>
    createEdgeLineSafe(edge, nodePositions).map(edge.edgeId -> _)
  }.toMap

  /* Updates existing edges' visuals or creates new ones if needed. */
  def updateEdges(updatedEdges: Iterable[Edge]): Map[String, Line] =
    updatedEdges.map { edge =>
      val id = edge.edgeId
      val line = edgeLines.get(id) match
        case Some(existing) =>
          updateLine(existing, edge, nodePositions)
          existing
        case None =>
          createEdgeLineSafe(edge, nodePositions).getOrElse(new Line())
      id -> line
    }.toMap

object EdgeLayer:

  def createEdgeLineSafe(edge: Edge, nodePositions: Map[String, LivePosition]): Option[Line] =
    for
      start <- nodePositions.get(edge.nodeA)
      end <- nodePositions.get(edge.nodeB)
    yield
      val (startX, startY) = start.get()
      val (endX, endY) = end.get()
      val line = new Line(startX, startY, endX, endY)
      line.setStroke(edgeColor(edge.typology, edge.isClose))
      line


  private def updateLine(line: Line, edge: Edge, nodePositions: Map[String, LivePosition]): Unit =
    val (startX, startY) = nodePositions(edge.nodeA).get()
    val (endX, endY) = nodePositions(edge.nodeB).get()
    line.setStartX(startX)
    line.setStartY(startY)
    line.setEndX(endX)
    line.setEndY(endY)
    line.setStroke(edgeColor(edge.typology, edge.isClose))

  /*  Returns the color to use for an edge based on its type and state. */
  def edgeColor(edgeType: EdgeType, isClose: Boolean): Color =
    if isClose then Color.Gray
    else
      edgeType match
        case EdgeType.Land => Color.Green
        case EdgeType.Sea  => Color.Blue
        case EdgeType.Air  => Color.Red
