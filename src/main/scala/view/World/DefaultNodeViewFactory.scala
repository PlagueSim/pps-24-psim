package view.world

import scalafx.scene.shape.{Circle, Shape}
import scalafx.scene.text.Text
import scalafx.scene.Cursor
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.Includes.*
import model.world.*

class DefaultNodeViewFactory(onMoved: () => Unit) extends NodeViewFactory:

  private case class LabelData(label: Text, offsetX: Double, offsetY: Double)

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

    Draggable.make(circle, posX, posY, (x, y) => {
      circle.centerX = x
      circle.centerY = y
    }, (dx, dy) => {
      updateLabelPositions(dx, dy, labels)
      onMoved()
    })

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

object Draggable:
  def make(
            shape: Shape,
            initialX: Double,
            initialY: Double,
            setPosition: (Double, Double) => Unit,
            onMove: (Double, Double) => Unit
          ): Unit =
    def onDrag(offsetX: Double, offsetY: Double): MouseEvent => Unit =
      e =>
        val newX = (e.sceneX - offsetX).max(20).min(780)
        val newY = (e.sceneY - offsetY).max(20).min(580)
        setPosition(newX, newY)
        onMove(newX, newY)

    shape.onMouseEntered = (_: MouseEvent) =>
      shape.cursor = Cursor.Hand

    shape.onMouseExited = (_: MouseEvent) =>
      shape.cursor = Cursor.Default

    shape.onMousePressed = (e: MouseEvent) =>
      val offsetX = e.sceneX - initialX
      val offsetY = e.sceneY - initialY
      shape.onMouseDragged = onDrag(offsetX, offsetY)
