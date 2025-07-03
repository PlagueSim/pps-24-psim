package view

import scalafx.scene.layout.Pane
import scalafx.scene.shape.{Circle, Line}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import model.World.*

class WorldView(world: World) extends Pane:

  // Simple layout positions for testing
  private val nodePositions: Map[String, (Double, Double)] = Map(
    "A" -> (100, 100),
    "B" -> (300, 150),
    "C" -> (200, 300)
  )

  // Draw edges
  for edge <- world.edges do
    val (x1, y1) = nodePositions(edge.nodeA)
    val (x2, y2) = nodePositions(edge.nodeB)

    val offset = edge.typology match
      case EdgeType.Land => (-8, -8)
      case EdgeType.Sea  => (0, 0)
      case EdgeType.Air  => (8, 8)

    val line = new Line:
      startX = x1 + offset._1
      startY = y1 + offset._2
      endX = x2 + offset._1
      endY = y2 + offset._2
      stroke = edge.typology match
        case EdgeType.Land => Color.Green
        case EdgeType.Sea  => Color.Blue
        case EdgeType.Air  => Color.Red
      strokeWidth = 2

    children += line

  // Draw nodes with labels
  for (nodeId, (x, y)) <- nodePositions do
    val nodeData = world.nodes(nodeId)

    val circle = new Circle:
      centerX = x
      centerY = y
      radius = 15
      fill = Color.LightGray
      stroke = Color.Black

    val labelId = new Text(x - 15, y - 20, s"Node: $nodeId")
    val labelPop = new Text(x - 20, y + 30, s"Pop: ${nodeData.population}")
    val labelInf = new Text(x - 20, y + 45, s"Infected: ${nodeData.infected}")

    children ++= Seq(circle, labelId, labelPop, labelInf)
