package view

import scalafx.geometry.Insets
import scalafx.scene.layout.{BorderPane, HBox, Priority, VBox}
import scalafx.scene.text.Text
import controller.PlagueViewController

class PlagueView extends BorderPane:
  private val controller = new PlagueViewController(this)

  val plagueInfos = new Text("EH")
  HBox.setHgrow(plagueInfos, Priority.Always)



  private val trsBtn = StdBtn("Transmission"):
    controller.show(TransmissionView())
  private val smptsBtn = StdBtn("Symptoms"):
    controller.show(SymptomsView())
  private val ablBtn = StdBtn("Abilities"):
    controller.show(AbilityView())
  private val leftSide = new VBox:
    children = Seq(trsBtn, smptsBtn, ablBtn)

  padding = Insets(10)
  center = plagueInfos
  left = leftSide
