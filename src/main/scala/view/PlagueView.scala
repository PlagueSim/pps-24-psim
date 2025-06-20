package view

import scalafx.geometry.Insets
import scalafx.scene.layout.{BorderPane, HBox, Priority}
import scalafx.scene.text.Text
 //TODO
class PlagueView extends BorderPane {
   val plagueInfos = new Text("SCALABBIAA!!!")
   HBox.setHgrow(plagueInfos, Priority.Always)

   padding = Insets(10)
   center = plagueInfos
}
