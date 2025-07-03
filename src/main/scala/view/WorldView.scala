package view

import scalafx.Includes.*
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.scene.layout.Pane
import scalafx.scene.shape.{Circle, Line}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.scene.input.MouseEvent
import model.World.*
import scala.collection.mutable

class WorldView(world: World) extends Pane:

  private val nodePositions: mutable.Map[String, (Double, Double)] = mutable.Map(
    "A" -> (100.0, 100.0),
    "B" -> (300.0, 150.0),
    "C" -> (200.0, 300.0)
  )

  private val edgeLines = mutable.Buffer.empty[Line]

  // Draw edges initially
  for edge <- world.edges do
    val line = createEdgeLine(edge)
    edgeLines += line
    children += line

  // Draw nodes
  for (nodeId, (x, y)) <- nodePositions do
    val nodeData = world.nodes(nodeId)

    val circle = new Circle:
      centerX = x
      centerY = y
      radius = 15
      fill = Color.LightGray
      stroke = Color.Black

    val labelId = new Text(s"Node: $nodeId")
    val labelPop = new Text(s"Pop: ${nodeData.population}")
    val labelInf = new Text(s"Infected: ${nodeData.infected}")

    updateLabelPositions(x, y, labelId, labelPop, labelInf)

    var dragOffsetX = 0.0
    var dragOffsetY = 0.0

    circle.onMousePressed = (e: MouseEvent) => {
      dragOffsetX = e.sceneX - circle.centerX.value
      dragOffsetY = e.sceneY - circle.centerY.value
    }

    circle.onMouseDragged = (e: MouseEvent) => {
      val newX = e.sceneX - dragOffsetX
      val newY = e.sceneY - dragOffsetY

      circle.centerX = newX
      circle.centerY = newY
      nodePositions(nodeId) = (newX, newY)

      updateLabelPositions(newX, newY, labelId, labelPop, labelInf)

      redrawEdges()
    }

    children ++= Seq(circle, labelId, labelPop, labelInf)

  private def createEdgeLine(edge: Edge): Line =
    val (x1, y1) = nodePositions(edge.nodeA)
    val (x2, y2) = nodePositions(edge.nodeB)
    val offset = edge.typology match
      case EdgeType.Land => (-8, -8)
      case EdgeType.Sea  => (0, 0)
      case EdgeType.Air  => (8, 8)
    new Line:
      startX = x1 + offset._1
      startY = y1 + offset._2
      endX = x2 + offset._1
      endY = y2 + offset._2
      stroke = edge.typology match
        case EdgeType.Land => Color.Green
        case EdgeType.Sea  => Color.Blue
        case EdgeType.Air  => Color.Red
      strokeWidth = 2

  private def redrawEdges(): Unit =
    children --= edgeLines.map(_.delegate)
    edgeLines.clear()

    for edge <- world.edges do
      val line = createEdgeLine(edge)
      edgeLines += line

    children.prependAll(edgeLines.map(_.delegate))

  private def updateLabelPositions(
                                    x: Double,
                                    y: Double,
                                    labelId: Text,
                                    labelPop: Text,
                                    labelInf: Text
                                  ): Unit =
    labelId.x = x - 15
    labelId.y = y - 20
    labelPop.x = x - 20
    labelPop.y = y + 30
    labelInf.x = x - 20
    labelInf.y = y + 45
