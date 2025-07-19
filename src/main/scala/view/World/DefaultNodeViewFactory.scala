package view.world

import scalafx.scene.shape.Circle
import scalafx.scene.text.Text
import scalafx.scene.Cursor
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.Includes.*
import model.world.*

class DefaultNodeViewFactory(onMoved: () => Unit) extends NodeViewFactory:

  case class LabelData(label: Text, offsetX: Double, offsetY: Double)

  override def createNode(id: String, data: Node, position: (Double, Double)): NodeView =
    val (posX, posY) = position

    val circle = new Circle:
      centerX = posX
      centerY = posY
      radius = 15
      fill = Color.LightGray
      stroke = Color.Black

    val labels = Seq(
      LabelData(new Text(s"Node: $id"), -15, -20),
      LabelData(new Text(s"Pop: ${data.population}"), -20, 30),
      LabelData(new Text(s"Infected: ${data.infected}"), -20, 45),
      LabelData(new Text(s"Died: ${data.died}"), -20, 60)
    )

    updateLabelPositions(posX, posY, labels)
    makeDraggable(circle, labels)

    NodeView(
      id = id,
      visuals = circle.delegate +: labels.map(_.label.delegate),
      position = () => (circle.centerX.value, circle.centerY.value),
      labelId = labels.head.label,
      labelPop = labels(1).label,
      labelInf = labels(2).label,
      labelDied = labels(3).label
    )

  private def updateLabelPositions(x: Double, y: Double, labels: Seq[LabelData]): Unit =
    labels.foreach { case LabelData(label, dx, dy) =>
      label.x = x + dx
      label.y = y + dy
    }

  private def makeDraggable(circle: Circle, labels: Seq[LabelData]): Unit =
    def onDrag(offsetX: Double, offsetY: Double): MouseEvent => Unit =
      e =>
        val newX = (e.sceneX - offsetX).max(20).min(780)
        val newY = (e.sceneY - offsetY).max(20).min(580)

        circle.centerX = newX
        circle.centerY = newY

        updateLabelPositions(newX, newY, labels)
        onMoved()

    circle.onMouseEntered = (_: MouseEvent) =>
      circle.cursor = Cursor.Hand

    circle.onMouseExited = (_: MouseEvent) =>
      circle.cursor = Cursor.Default

    circle.onMousePressed = (e: MouseEvent) =>
      val offsetX = e.sceneX - circle.centerX.value
      val offsetY = e.sceneY - circle.centerY.value
      circle.onMouseDragged = onDrag(offsetX, offsetY)
