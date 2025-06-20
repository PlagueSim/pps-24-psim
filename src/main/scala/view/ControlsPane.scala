package view
import controller.ViewController
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, ProgressBar}
import scalafx.scene.layout.BorderPane

class ControlsPane(val controller: ViewController) extends BorderPane {
  
  def standardButton(txt: String)(e: => Unit): Button = new Button {
    text = txt
    padding = Insets(20, 60, 20, 60)
    onAction = _ => e
  }

  val plagueButton = standardButton("Plague") {
    println("Plague button pressed")
    controller.show("PlagueInfo")
  }

  val worldButton = standardButton("World") {
    println("World button pressed")
    controller.show("WorldInfo")
  }

  val progressBar = new ProgressBar {
    progress = 0.35
    prefWidth = 200
    prefHeight = 25
  }

  left = plagueButton
  center = progressBar
  right = worldButton
  padding = Insets(10)
}
