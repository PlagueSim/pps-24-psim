package view.world

import scalafx.scene.text.Text
import javafx.scene.Node as FxNode

case class NodeView(
                     visuals: Seq[FxNode],
                     position: () => (Double, Double),
                     labels: Map[String, Text]
                   ):
  /*
   * Updates the labels of this NodeView using values from another NodeView.
   * Only "pop", "inf", and "died" labels are updated.
   */
  def updateLabels(from: NodeView): Unit =
    this.labels.get("pop").foreach(_.text = from.labels("pop").text())
    this.labels.get("inf").foreach(_.text = from.labels("inf").text())
    this.labels.get("died").foreach(_.text = from.labels("died").text())

  /*
   * Returns a copy of this NodeView with labels updated from the given Node model.
   * The text values are recreated based on the model's data.
   */
  def withUpdatedLabelsFromModel(node: model.world.Node): NodeView =
    val updated = labels.map {
      case ("pop", lbl) => "pop" -> new Text(s"Pop: ${node.population}")
      case ("inf", lbl) => "inf" -> new Text(s"Infected: ${node.infected}")
      case ("died", lbl) => "died" -> new Text(s"Died: ${node.died}")
      case other => other
    }
    this.copy(labels = updated)


object NodeView:
  /**
   * Factory method to create a NodeView from individual label elements.
   *
   * @param id the ID of the node (used only for the label)
   * @param visuals the visual JavaFX nodes representing the NodeView
   * @param position a function returning the current position of the node
   * @param labelId the label showing the node ID
   * @param labelPop the label showing the population
   * @param labelInf the label showing the infected count
   * @param labelDied the label showing the death count
   * @return a new NodeView instance
   */
  def apply(
             id: String,
             visuals: Seq[FxNode],
             position: () => (Double, Double),
             labelId: Text,
             labelPop: Text,
             labelInf: Text,
             labelDied: Text
           ): NodeView =
    NodeView(
      visuals = visuals,
      position = position,
      labels = Map(
        "id"   -> labelId,
        "pop"  -> labelPop,
        "inf"  -> labelInf,
        "died" -> labelDied
      )
    )
