package view

import controller.ViewController
import model.World.WorldFactory
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.ProgressBar
import scalafx.scene.layout.BorderPane

object MainScene:
  def apply(): Scene = new Scene:
    root = MainView()

class MainView extends BorderPane:
  private val controller = ViewController(this)
  private val mapPane = new WorldView(WorldFactory.mockWorld())
  private val plgPane = PlagueView()
  private val controlPane = ControlPane(controller)

  center = mapPane
  bottom = controlPane
end MainView

object ControlPane:
  def apply(controller: ViewController): BorderPane = new BorderPane:
    private val plagueButton = StdButton("Plague"):
      controller.show(PlagueView())

    private val worldButton = StdButton("World"):
      controller.show(MapView())

    private val progressBar = new ProgressBar:
      progress = 0.35
      prefWidth = 200
      prefHeight = 25

    left = plagueButton
    center = progressBar
    right = worldButton
    padding = Insets(10)
end ControlPane
