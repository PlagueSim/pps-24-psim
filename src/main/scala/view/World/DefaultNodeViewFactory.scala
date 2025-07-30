package view.world

import scalafx.scene.Group
import scalafx.scene.shape.{Circle, Shape}
import scalafx.scene.text.Text
import scalafx.scene.Cursor
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import scalafx.Includes.*
import model.world.*

/**
 * A factory for creating visual representations of nodes using circles and labels.
 * Each node is draggable and displays information about its state.
 *
 * @param onMoved A callback invoked when a node is dragged.
 */

class DefaultNodeViewFactory(onMoved: () => Unit) extends NodeViewFactory:

  private case class LabelData(label: Text, offsetX: Double, offsetY: Double)

  /**
   * Creates a draggable NodeView consisting of a circle and several labels
   * showing node-related data (population, infected, dead).
   *
   */
  override def createNode(id: String, data: Node, position: (Double, Double)): NodeView =
    val (posX, posY) = position

    val circle = new Circle {
      radius = 15
      fill = Color.LightGray
      stroke = Color.Black
    }
    circle.relocate(-15, -15) // center circle at (0, 0)

    val labels = Seq(
      LabelData(new Text(s"Node: $id"), -15, -35),
      LabelData(new Text(s"Pop: ${data.population}"), -20, 20), 
      LabelData(new Text(s"Infected: ${data.infected}"), -20, 35),
      LabelData(new Text(s"Died: ${data.died}"), -20, 50) 
    )

    labels.foreach { case LabelData(lbl, dx, dy) => lbl.relocate(dx, dy) }

    val group = new Group(circle +: labels.map(_.label): _*)
    group.relocate(posX, posY)

    Draggable.make(group, posX, posY, (x, y) => group.relocate(x, y), (_, _) => onMoved())

    NodeView(
      id = id,
      visuals = Seq(group),
      position = () => (group.layoutX.value, group.layoutY.value),
      labelId = labels.head.label,
      labelPop = labels(1).label,
      labelInf = labels(2).label,
      labelDied = labels(3).label
    )

  private def updateLabelPositions(x: Double, y: Double, labels: Seq[LabelData]): Unit =
    labels.foreach { case LabelData(label, dx, dy) =>
      label.relocate(x + dx, y + dy)
    }

object Draggable:
  /**
   * Makes a shape draggable on the scene. Handles mouse interaction and position update.
   *
   * @param shape       The shape to be made draggable.
   * @param initialX    Initial X coordinate.
   * @param initialY    Initial Y coordinate.
   * @param setPosition Function to apply the new position.
   * @param onMove      Callback called after every drag movement.
   */
  def make(
            node: scalafx.scene.Node,
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

    node.onMouseEntered = (_: MouseEvent) =>
      node.cursor = Cursor.Hand

    node.onMouseExited = (_: MouseEvent) =>
      node.cursor = Cursor.Default

    node.onMousePressed = (e: MouseEvent) =>
      val offsetX = e.sceneX - initialX
      val offsetY = e.sceneY - initialY
      node.onMouseDragged = onDrag(offsetX, offsetY)
