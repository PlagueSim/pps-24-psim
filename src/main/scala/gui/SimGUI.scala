package gui

import gui.SimGUI.stage
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, BottomLeft, BottomRight, Center, TopLeft, TopRight}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, HBox, Pane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.stage.Screen

object SimGUI extends JFXApp3{

  override def start():Unit = {
    val X = Screen.primary.visualBounds.width
    val Y = Screen.primary.visualBounds.height

    // parte superiore con la mappa del mondo
    val map = new HBox() {
      alignmentInParent = Center
      margin = Insets(Y/3,X/3,Y/3,X/3)
      children = new Text {

        text = "Mappa del Mondo"
      }
    }

    // parte inferiore con i pulsanti ecc...
    val plgBtn = new Button {
      padding = Insets(20, 60, 20, 60)
      text = "Plague"
    }
    

    val wrldBtn = new Button {
      padding = Insets(20, 60, 20, 60)
      text = "World"
    }

    // potenzialmente da aggiungere le info di diffusione
//  val info = ???

    val btns = new BorderPane() {
      alignmentInParent = BottomCenter
      left = plgBtn
      right = wrldBtn
    }

    // layout principale
    val mainLayout = new BorderPane {
      center = map
      bottom = btns
    }

    val mainScene = new Scene {
      content = mainLayout
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Plague Sim"
      scene = mainScene
    }
  }

}
