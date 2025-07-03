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
import scala.math.*

class WorldView(world: World) extends Pane:

  private val layoutRadius = 200.0
  private val layoutCenterX = 400.0
  private val layoutCenterY = 250.0
  private val nodeIds = world.nodes.keys.toSeq.sorted
  private val angleStep = 2 * Pi / nodeIds.size

  private val nodePositions: mutable.Map[String, (Double, Double)] =
    mutable.Map.from(
      nodeIds.zipWithIndex.map { case (id, i) =>
        val angle = i * angleStep
        val x = layoutCenterX + layoutRadius * cos(angle)
        val y = layoutCenterY + layoutRadius * sin(angle)
        id -> (x, y)
      }
    )

  private val edgeLines = mutable.Buffer.empty[Line]

  extension (d: Double)
    def clamp(min: Double, max: Double): Double =
      d.max(min).min(max)

  private def edgeStyle(edgeType: EdgeType): ((Int, Int), Color) = edgeType match
    case EdgeType.Land => ((-8, -8), Color.Green)
    case EdgeType.Sea  => ((0, 0), Color.Blue)
    case EdgeType.Air  => ((8, 8), Color.Red)

  private case class NodeView(
                               id: String,
                               circle: Circle,
                               labelId: Text,
                               labelPop: Text,
                               labelInf: Text
                             )

  children ++= world.edges.map(createEdgeLine)

  private val nodeViews: Seq[NodeView] = nodePositions.toSeq.map { case (nodeId, (x, y)) =>
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

    makeDraggable(circle, nodeId, (labelId, labelPop, labelInf))

    NodeView(nodeId, circle, labelId, labelPop, labelInf)
  }

  children ++= nodeViews.flatMap(nv => Seq(nv.circle, nv.labelId, nv.labelPop, nv.labelInf))

  /** Create a Line for an edge */
  private def createEdgeLine(edge: Edge): Line =
    val (x1, y1) = nodePositions(edge.nodeA)
    val (x2, y2) = nodePositions(edge.nodeB)
    val ((dx, dy), color) = edgeStyle(edge.typology)
    val line = new Line:
      startX = x1 + dx
      startY = y1 + dy
      endX = x2 + dx
      endY = y2 + dy
      stroke = color
      strokeWidth = 2
    edgeLines += line
    line

  private def redrawEdges(): Unit =
    children --= edgeLines.map(_.delegate)
    edgeLines.clear()
    children.prependAll(world.edges.map(createEdgeLine).map(_.delegate))

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

  private def makeDraggable(
                             circle: Circle,
                             nodeId: String,
                             labels: (Text, Text, Text)
                           ): Unit =
    var dragOffsetX = 0.0
    var dragOffsetY = 0.0

    circle.onMouseEntered = (_: MouseEvent) => {
      circle.cursor = scalafx.scene.Cursor.Hand
    }

    circle.onMouseExited = (_: MouseEvent) => {
      circle.cursor = scalafx.scene.Cursor.Default
    }

    circle.onMousePressed = (e: MouseEvent) => {
      dragOffsetX = e.sceneX - circle.centerX.value
      dragOffsetY = e.sceneY - circle.centerY.value
    }

    circle.onMouseDragged = (e: MouseEvent) => {
      val newX = e.sceneX - dragOffsetX
      val newY = e.sceneY - dragOffsetY

      val clampedX = newX.clamp(20, 780)
      val clampedY = newY.clamp(20, 580)

      circle.centerX = clampedX
      circle.centerY = clampedY
      nodePositions(nodeId) = (clampedX, clampedY)

      updateLabelPositions(clampedX, clampedY, labels._1, labels._2, labels._3)
      redrawEdges()
    }

