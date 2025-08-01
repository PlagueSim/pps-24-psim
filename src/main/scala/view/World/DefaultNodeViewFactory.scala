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
 * @param onMoved A callback invoked when a node is dragged. (update all edges)
 */

class DefaultNodeViewFactory(onMoved: () => Unit) extends NodeViewFactory:

  private case class LabelData(label: Text, offsetX: Double, offsetY: Double)

  /**
   * Creates a draggable NodeView consisting of a circle and several labels
   * showing node-related data (population, infected, dead).
   * 
   * @param id The unique identifier for the node.
   * @param data The Node data containing population, infected, and died counts.
   * @param position The initial position of the node in the scene.
   *                 
   * @return A NodeView containing the visual representation of the node.
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

    Draggable.make(group, (x, y) => group.relocate(x, y), (_, _) => onMoved())


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
  /*
   * Makes a shape draggable on the scene. Handles mouse interaction and position update.
   */
  def make(
            node: scalafx.scene.Node,
            setPosition: (Double, Double) => Unit,
            onMove: (Double, Double) => Unit
          ): Unit =

    /**
     * Handles the dragging of the node.
     * Calculates the new position based on mouse movement,
     * ensures it stays within bounds, and updates the position.
     * @param offsetX The initial X offset from the mouse to the node's position.
     * @param offsetY The initial Y offset from the mouse to the node's position.
     *                
     * @return A function that takes a MouseEvent and updates the node's position.
     */
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
      val offsetX = e.sceneX - node.layoutX.value
      val offsetY = e.sceneY - node.layoutY.value
      node.onMouseDragged = onDrag(offsetX, offsetY)

