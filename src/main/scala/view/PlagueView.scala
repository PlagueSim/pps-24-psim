package view

import controller.ViewController
import scalafx.geometry.Insets
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{BorderPane, HBox, Priority, VBox}
import scalafx.scene.text.Text

class PlagueView extends BorderPane:
  private val controller = ViewController(this)

  val plagueInfos = Text("EH")
  HBox.setHgrow(plagueInfos, Priority.Always)



  private val trsBtn = StdButton("Transmission"):
    controller.show(TransmissionView())
  private val smptsBtn = StdButton("Symptoms"):
    controller.show(SymptomsView())
  private val ablBtn = StdButton("Abilities"):
    controller.show(AbilityView())
  private val topBar = new HBox:
    children = Seq(trsBtn, smptsBtn, ablBtn)


  padding = Insets(10)
  center = plagueInfos
  top = topBar
