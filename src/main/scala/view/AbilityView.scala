package view

import scalafx.geometry.Insets
import scalafx.geometry.Pos.Center
import scalafx.scene.layout.{HBox, Priority, Region}
import scalafx.scene.text.Text

class AbilityView extends HBox:
  val abilities = new Text("EH, ABILITIES")

  //    new Region {
  //    style = "-fx-background-color: #2a2a2a; -fx-border-color: white;"
  //  }
  HBox.setHgrow(abilities, Priority.Always)

  alignment = Center
  padding = Insets(10)
  children = abilities
