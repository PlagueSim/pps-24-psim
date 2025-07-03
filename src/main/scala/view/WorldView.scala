package view

import scalafx.Includes.*
import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.application.Platform
import scalafx.scene.Cursor
import scalafx.scene.layout.Pane
import scalafx.scene.shape.{Circle, Line}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.scene.input.MouseEvent
import model.World.*
import scala.math.*

class WorldView(world: World) extends Pane:

  private val layoutRadius = 200.0
  private val layoutCenterX = 400.0
  private val layoutCenterY = 250.0

  private val nodeIds = world.nodes.keys.toSeq.sorted
  private val angleStep = 2 * Pi / nodeIds.size

  extension (d: Double)
    private def clamp(min: Double, max: Double): Double =
      d.max(min).min(max)

  private def edgeStyle(edgeType: EdgeType): ((Int, Int), Color) = edgeType match
    case EdgeType.Land => ((-8, -8), Color.Green)
    case EdgeType.Sea  => ((0, 0), Color.Blue)
    case EdgeType.Air  => ((8, 8), Color.Red)

  private case class NodeView(
                               id: String,
                               circle: Circle,
                               labels: (Text, Text, Text)
                             ):
    def position: (Double, Double) =
      (circle.centerX.value, circle.centerY.value)

  private def initialLayout: Seq[(String, (Double, Double))] =
    nodeIds.zipWithIndex.map { (id, i) =>
      val angle = i * angleStep
      val x = layoutCenterX + layoutRadius * cos(angle)
      val y = layoutCenterY + layoutRadius * sin(angle)
      id -> (x, y)
    }

  private val nodeViews: Seq[NodeView] =
    initialLayout.map { case (id, (x, y)) => createNodeView(id, x, y) }

  private def addNodeViews(): Unit =
    val nodesAndLabels = nodeViews.flatMap { nv =>
      Seq(nv.circle.delegate) ++ nv.labels.productIterator.map(_.asInstanceOf[Text].delegate)
    }
    children ++= nodesAndLabels

  private def addEdges(): Unit =
    Platform.runLater { redrawEdges() }

  addNodeViews()
  addEdges()

  private def createNodeView(id: String, x: Double, y: Double): NodeView =
    val nodeData = world.nodes(id)
    val circle = buildCircle(x, y)
    val labels = buildLabels(id, nodeData, x, y)
    makeDraggable(circle, labels)
    NodeView(id, circle, labels)

  private def buildCircle(x: Double, y: Double): Circle =
    new Circle:
      centerX = x
      centerY = y
      radius = 15
      fill = Color.LightGray
      stroke = Color.Black

  private def buildLabels(id: String, node: Node, x: Double, y: Double): (Text, Text, Text) =
    val labelId = new Text(s"Node: $id")
    val labelPop = new Text(s"Pop: ${node.population}")
    val labelInf = new Text(s"Infected: ${node.infected}")
    updateLabelPositions(x, y, labelId, labelPop, labelInf)
    (labelId, labelPop, labelInf)

  private def createEdgeLine(edge: Edge): Line =
    val ((dx, dy), color) = edgeStyle(edge.typology)
    val (x1, y1) = nodeViews.find(_.id == edge.nodeA).get.position
    val (x2, y2) = nodeViews.find(_.id == edge.nodeB).get.position
    new Line:
      startX = x1 + dx
      startY = y1 + dy
      endX = x2 + dx
      endY = y2 + dy
      stroke = color
      strokeWidth = 2

  private def currentEdges: Seq[Line] =
    world.edges.toSeq.map(createEdgeLine)

  private def redrawEdges(): Unit =
    val oldEdges = children.collect {
      case n if n.isInstanceOf[javafx.scene.shape.Line] => n
    }
    children --= oldEdges
    children.prependAll(currentEdges.map(_.delegate))

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
                             labels: (Text, Text, Text)
                           ): Unit =
    var dragOffsetX = 0.0
    var dragOffsetY = 0.0

    circle.onMouseEntered = (_: MouseEvent) =>
      circle.cursor = Cursor.Hand

    circle.onMouseExited = (_: MouseEvent) =>
      circle.cursor = Cursor.Default

    circle.onMousePressed = (e: MouseEvent) =>
      dragOffsetX = e.sceneX - circle.centerX.value
      dragOffsetY = e.sceneY - circle.centerY.value

    circle.onMouseDragged = (e: MouseEvent) =>
      val clampedX = (e.sceneX - dragOffsetX).clamp(20, 780)
      val clampedY = (e.sceneY - dragOffsetY).clamp(20, 580)

      circle.centerX = clampedX
      circle.centerY = clampedY

      updateLabelPositions(clampedX, clampedY, labels._1, labels._2, labels._3)
      redrawEdges()
