package view.world

import scalafx.scene.text.Text

case class NodeView(
                     id: String,
                     visuals: Seq[Any],
                     position: () => (Double, Double),
                     labels: Map[String, Text]
                   )

object NodeView:
  def apply(
             id: String,
             visuals: Seq[Any],
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
        "id" -> labelId,
        "pop" -> labelPop,
        "inf" -> labelInf,
        "died" -> labelDied
      )
    )
