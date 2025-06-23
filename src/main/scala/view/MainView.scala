package view

import controller.MainViewController
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.ProgressBar
import scalafx.scene.layout.BorderPane

object MainScene:
  def apply(): Scene = new Scene:
    root = new MainView

class MainView extends BorderPane:
  private val controller = new MainViewController(this)
  private val mapPane = new MapView
  private val plgPane = new PlagueView
  private val controlPane = ControlPane(controller)

  def show(s: String): Unit = this.center = s match
      case "PlagueInfo" => plgPane
      case "WorldInfo" => mapPane
      case _ => mapPane

  center = mapPane
  bottom = controlPane
end MainView

object ControlPane:
  def apply(controller: MainViewController): BorderPane = new BorderPane:
    private val plagueButton = StdBtn("Plague"):
      controller.show("PlagueInfo")

    private val worldButton = StdBtn("World"):
      controller.show("WorldInfo")

    private val progressBar = new ProgressBar:
      progress = 0.35
      prefWidth = 200
      prefHeight = 25

    left = plagueButton
    center = progressBar
    right = worldButton
    padding = Insets(10)
end ControlPane
