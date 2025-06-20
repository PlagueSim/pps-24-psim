package view

import controller.ViewController
import scalafx.scene.Scene
import scalafx.scene.layout.BorderPane

class MainView extends Scene {
  private val controller = ViewController(this)
  private val mapPane = new MapView
  private val plgPane = new PlagueView
  private val controlsPane = new ControlsPane(controller)

  root = new BorderPane {
    center = mapPane
    bottom = controlsPane
  }

  def plgInfo(): Unit = this.root = new BorderPane {
    center = plgPane
    bottom = controlsPane
  }

  def wrldInfo(): Unit = this.root = new BorderPane {
    center = mapPane
    bottom = controlsPane
  }
}
