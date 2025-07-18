package view.world

import scalafx.scene.shape.Circle
import scalafx.scene.text.Text
import scalafx.scene.Cursor
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.Includes.*
import model.world.*

class DefaultNodeViewFactory(onMoved: () => Unit) extends NodeViewFactory:

  override def createNode(id: String, data: Node, position: (Double, Double)): NodeView =
    val (posX, posY) = position

    val circle = new Circle:
      centerX = posX
      centerY = posY
      radius = 15
      fill = Color.LightGray
      stroke = Color.Black

    val labelId = new Text(s"Node: $id")
    val labelPop = new Text(s"Pop: ${data.population}")
    val labelInf = new Text(s"Infected: ${data.infected}")
    val labelDied = new Text(s"Died: ${data.died}")

    updateLabelPositions(posX, posY, labelId, labelPop, labelInf, labelDied)
    makeDraggable(circle, labelId, labelPop, labelInf, labelDied)

    NodeView(
      id = id,
      visuals = Seq(circle.delegate, labelId.delegate, labelPop.delegate, labelInf.delegate, labelDied.delegate),
      position = () => (circle.centerX.value, circle.centerY.value),
      labelId = labelId,
      labelPop = labelPop,
      labelInf = labelInf,
      labelDied = labelDied
    )

  private def updateLabelPositions(
                                    x: Double,
                                    y: Double,
                                    labelId: Text,
                                    labelPop: Text,
                                    labelInf: Text,
                                    labelDied: Text
                                  ): Unit =
    labelId.x = x - 15
    labelId.y = y - 20
    labelPop.x = x - 20
    labelPop.y = y + 30
    labelInf.x = x - 20
    labelInf.y = y + 45
    labelDied.x = x - 20
    labelDied.y = y + 60

  private def makeDraggable(
                             circle: Circle,
                             labelId: Text,
                             labelPop: Text,
                             labelInf: Text,
                             labelDied: Text
                           ): Unit =
    def onDrag(startOffsetX: Double, startOffsetY: Double): MouseEvent => Unit =
      e =>
        val newX = (e.sceneX - startOffsetX).max(20).min(780)
        val newY = (e.sceneY - startOffsetY).max(20).min(580)

        circle.centerX = newX
        circle.centerY = newY
        updateLabelPositions(newX, newY, labelId, labelPop, labelInf, labelDied)

        onMoved()

    circle.onMouseEntered = (_: MouseEvent) =>
      circle.cursor = Cursor.Hand

    circle.onMouseExited = (_: MouseEvent) =>
      circle.cursor = Cursor.Default

    circle.onMousePressed = (e: MouseEvent) =>
      val offsetX = e.sceneX - circle.centerX.value
      val offsetY = e.sceneY - circle.centerY.value
      circle.onMouseDragged = onDrag(offsetX, offsetY)
