package view.plague

import scalafx.geometry.Insets
import scalafx.geometry.Pos.Center
import scalafx.scene.layout.{HBox, Priority, Region}
import scalafx.scene.text.Text

class TransmissionView extends HBox:
  val trs = new Text("EH, TRANSMISSIONS")

  //    new Region {
  //    style = "-fx-background-color: #2a2a2a; -fx-border-color: white;"
  //  }
  HBox.setHgrow(trs, Priority.Always)

  alignment = Center
  padding = Insets(10)
  children = trs
