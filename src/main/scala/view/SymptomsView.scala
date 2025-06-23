package view

import scalafx.geometry.Insets
import scalafx.geometry.Pos.Center
import scalafx.scene.layout.{HBox, Priority, Region}
import scalafx.scene.text.Text

class SymptomsView extends HBox:
  val symptoms = new Text("EH, SYMPTOMS")

  //    new Region {
  //    style = "-fx-background-color: #2a2a2a; -fx-border-color: white;"
  //  }
  HBox.setHgrow(symptoms, Priority.Always)

  alignment = Center
  padding = Insets(10)
  children = symptoms
