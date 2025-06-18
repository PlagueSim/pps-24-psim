
package gui

import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.geometry.Pos.Center
import scalafx.scene.Scene
import scalafx.scene.control.{Button, ProgressBar}
import scalafx.scene.layout.{BorderPane, HBox, Priority, Region}
import scalafx.stage.Screen

object SimGUI extends JFXApp3 {

  override def start(): Unit = {
    val X = Screen.primary.bounds.width
    val Y = Screen.primary.bounds.height

    // Zona Mappa
    val mapRegion = new Region {
      style = "-fx-background-color: #2a2a2a; -fx-border-color: white;"
    }
    HBox.setHgrow(mapRegion, Priority.Always)

    val map = new HBox {
      alignment = Center
      padding = Insets(10)
      children = mapRegion
    }

    // Pulsanti
    val plgBtn = new Button {
      text = "Plague"
      padding = Insets(20, 60, 20, 60)
    }

    val wrldBtn = new Button {
      text = "World"
      padding = Insets(20, 60, 20, 60)
    }

    // ProgressBar
    val prgrsBar = new ProgressBar {
      progress = 0.35
      prefWidth = 200
      prefHeight = 25
    }
    HBox.setHgrow(prgrsBar, Priority.Always)

    val btns = new BorderPane {
      left = plgBtn
      center = prgrsBar
      right = wrldBtn
      padding = Insets(10)
    }

    // Layout principale
    val mainLayout = new BorderPane {
      center = map
      bottom = btns
    }

    val mainScene = new Scene {
      root = mainLayout
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Plague Sim"
      scene = mainScene
      width = X * 0.66
      height = Y * 0.66
      minWidth = X * 0.33
      minHeight = Y * 0.33
    }
  }
}
