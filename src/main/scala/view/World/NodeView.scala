package view.world

import scalafx.scene.text.Text
import javafx.scene.Node as FxNode

case class NodeView(
                     id: String,
                     visuals: Seq[FxNode],
                     position: () => (Double, Double),
                     labels: Map[String, Text]
                   ):
  def updateLabels(from: NodeView): Unit =
    this.labels.get("pop").foreach(_.text = from.labels("pop").text())
    this.labels.get("inf").foreach(_.text = from.labels("inf").text())
    this.labels.get("died").foreach(_.text = from.labels("died").text())

  def withUpdatedLabelsFromModel(node: model.world.Node): NodeView =
    val updated = labels.map {
      case ("pop", lbl) => "pop" -> new Text(s"Pop: ${node.population}")
      case ("inf", lbl) => "inf" -> new Text(s"Infected: ${node.infected}")
      case ("died", lbl) => "died" -> new Text(s"Died: ${node.died}")
      case other => other
    }
    this.copy(labels = updated)


object NodeView:
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
      id = id,
      visuals = visuals,
      position = position,
      labels = Map(
        "id"   -> labelId,
        "pop"  -> labelPop,
        "inf"  -> labelInf,
        "died" -> labelDied
      )
    )
