package view.world

import scalafx.scene.text.Text

case class NodeView(
                     id: String,
                     visuals: Seq[Any],              
                     position: () => (Double, Double),
                     labelId: Text,
                     labelPop: Text,
                     labelInf: Text,
                     labelDied: Text
                   )
