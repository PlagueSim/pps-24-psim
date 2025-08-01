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

  /** Updates existing edges' visuals or creates new ones if needed.
   * @param updatedEdges Iterable of edges to update.
   *                     
   * @return A map of edge IDs to their corresponding updated Line objects.
   * */
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

/*
* EdgeLayer provides methods to create and update JavaFX Line objects
* representing edges in the simulation.
* It ensures that edges are created with the correct positions and colors
* based on their type and state.
* */
object EdgeLayer:

  /**
  * Creates a JavaFX Line for the given edge if both nodes are present in the nodePositions map.
  * @param edge The edge to create a line for.
  * @param nodePositions A map of node IDs to their LivePosition objects.
   *                      
  * @return An Option containing the created Line if both nodes are found, otherwise None.
  * */
  def createEdgeLineSafe(edge: Edge, nodePositions: Map[String, LivePosition]): Option[Line] =
    for
      start <- nodePositions.get(edge.nodeA)
      end <- nodePositions.get(edge.nodeB)
    yield
      val (startX, startY) = start.get()
      val (endX, endY) = end.get()
      val offset = edgeOffset(edge.typology)
      val line = createOffsetLine(startX, startY, endX, endY, offset)
      line.setStroke(edgeColor(edge.typology, edge.isClose))
      line

  /**
   * Updates the position and color of an existing JavaFX Line based on the edge and node positions.
   * @param line The Line object to update.
   * @param edge The Edge object containing the nodes and type.
   * @param nodePositions A map of node IDs to their LivePosition objects.
   * This method modifies the Line's start and end coordinates and updates its stroke color
   * */
  def updateLine(line: Line, edge: Edge, nodePositions: Map[String, LivePosition]): Unit =
    val (startX, startY) = nodePositions(edge.nodeA).get()
    val (endX, endY) = nodePositions(edge.nodeB).get()
    val offset = edgeOffset(edge.typology)

    val updated = createOffsetLine(startX, startY, endX, endY, offset)

    line.setStartX(updated.getStartX)
    line.setStartY(updated.getStartY)
    line.setEndX(updated.getEndX)
    line.setEndY(updated.getEndY)
    line.setStroke(edgeColor(edge.typology, edge.isClose))


  /*  Returns the color to use for an edge based on its type and state. */
  private def edgeColor(edgeType: EdgeType, isClose: Boolean): Color =
    if isClose then Color.Gray
    else
      edgeType match
        case EdgeType.Land => Color.Green
        case EdgeType.Sea  => Color.Blue
        case EdgeType.Air  => Color.Red

  /* Creates a Line object representing an edge with an offset. */
  private def createOffsetLine(startX: Double, startY: Double, endX: Double, endY: Double, offset: Double): Line =
    val dx = endX - startX
    val dy = endY - startY
    val length = math.hypot(dx, dy).max(0.001)

    val normX = -dy / length
    val normY = dx / length

    val offsetX = normX * offset
    val offsetY = normY * offset

    new Line(startX + offsetX, startY + offsetY, endX + offsetX, endY + offsetY)

  /* represent the offset based on edge typology*/
  private def edgeOffset(edgeType: EdgeType): Double = edgeType match
    case EdgeType.Land => 0
    case EdgeType.Sea  => 6.0
    case EdgeType.Air  => -6.0