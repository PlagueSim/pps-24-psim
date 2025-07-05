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

object NodeId:
  opaque type NodeId = String
  def apply(s: String): NodeId = s
  extension (id: NodeId)
    def value: String = id

import NodeId.*

class WorldView(world: World) extends Pane:

  private val layoutRadius = 200.0
  private val layoutCenterX = 400.0
  private val layoutCenterY = 250.0

  private val nodeIds: Seq[NodeId] = world.nodes.keys.toSeq.sorted.map(NodeId(_))

  private val angleStep = 2 * Pi / nodeIds.size

  extension (d: Double)
    private def clamp(min: Double, max: Double): Double =
      d.max(min).min(max)

  private val edgeStyle: Map[EdgeType, ((Int, Int), Color)] = Map(
    EdgeType.Land -> ((-8, -8), Color.Green),
    EdgeType.Sea -> ((0, 0), Color.Blue),
    EdgeType.Air -> ((8, 8), Color.Red)
  )

  private case class NodeView(
                               id: NodeId,
                               circle: Circle,
                               labelId: Text,
                               labelPop: Text,
                               labelInf: Text
                             ):
    def position: (Double, Double) =
      (circle.centerX.value, circle.centerY.value)

    def visuals: Seq[javafx.scene.Node] =
      Seq(
        circle.delegate,
        labelId.delegate,
        labelPop.delegate,
        labelInf.delegate
      )

  private def initialLayout: Seq[(NodeId, (Double, Double))] =
    nodeIds.zipWithIndex.map {
      case (id, i) =>
        val angle = i * angleStep
        val x = layoutCenterX + layoutRadius * cos(angle)
        val y = layoutCenterY + layoutRadius * sin(angle)
        id -> (x, y)
    }

  private val nodeViews: Seq[NodeView] =
    initialLayout.map { case (id, (x, y)) => createNodeView(id, x, y) }

  private def addNodeViews(): Unit =
    children ++= nodeViews.flatMap(_.visuals)

  private def addEdges(): Unit =
    Platform.runLater(redrawEdges())

  addNodeViews()
  addEdges()

  private def createNodeView(id: NodeId, x: Double, y: Double): NodeView =
    val nodeData = world.nodes(id.value)
    val circle = buildCircle(x, y)
    val (labelId, labelPop, labelInf) = buildLabels(id, nodeData, x, y)
    makeDraggable(circle, id, (labelId, labelPop, labelInf))
    NodeView(id, circle, labelId, labelPop, labelInf)

  private def buildCircle(x: Double, y: Double): Circle =
    new Circle:
      centerX = x
      centerY = y
      radius = 15
      fill = Color.LightGray
      stroke = Color.Black

  private def buildLabels(
                           id: NodeId,
                           node: Node,
                           x: Double,
                           y: Double
                         ): (Text, Text, Text) =
    val labelId = new Text(s"Node: ${id.value}")
    val labelPop = new Text(s"Pop: ${node.population}")
    val labelInf = new Text(s"Infected: ${node.infected}")
    updateLabelPositions(x, y, labelId, labelPop, labelInf)
    (labelId, labelPop, labelInf)

  private def createEdgeLine(edge: Edge): Line =
    val ((dx, dy), color) = edgeStyle(edge.typology)

    val nodeA = nodeViews.find(_.id.value == edge.nodeA) match
      case Some(node) => node
      case None => sys.error(s"Node '${edge.nodeA}' not found")

    val nodeB = nodeViews.find(_.id.value == edge.nodeB) match
      case Some(node) => node
      case None => sys.error(s"Node '${edge.nodeB}' not found")

    val (x1, y1) = nodeA.position
    val (x2, y2) = nodeB.position

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
    children --= children.collect {
      case n if n.isInstanceOf[javafx.scene.shape.Line] => n
    }
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
                             id: NodeId,
                             labels: (Text, Text, Text)
                           ): Unit =
    def onDrag(startX: Double, startY: Double): MouseEvent => Unit =
      e =>
        val clampedX = (e.sceneX - startX).clamp(20, 780)
        val clampedY = (e.sceneY - startY).clamp(20, 580)
        circle.centerX = clampedX
        circle.centerY = clampedY
        updateLabelPositions(clampedX, clampedY, labels._1, labels._2, labels._3)
        redrawEdges()

    circle.onMouseEntered = (_: MouseEvent) =>
      circle.cursor = Cursor.Hand

    circle.onMouseExited = (_: MouseEvent) =>
      circle.cursor = Cursor.Default

    circle.onMousePressed = (e: MouseEvent) =>
      val startX = e.sceneX - circle.centerX.value
      val startY = e.sceneY - circle.centerY.value
      circle.onMouseDragged = onDrag(startX, startY)
