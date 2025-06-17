package gui

import gui.SimGUI.stage
import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.{BorderPane, HBox, Pane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.stage.Screen

object SimGUI extends JFXApp3{

  override def start():Unit = {
    val X = Screen.primary.bounds.width
    val Y = Screen.primary.bounds.height

    // parte superiore con la mappa del mondo
    val top = new BorderPane {
      children = new Text {

        text = "Mappa del Mondo"
      }
    }

    // parte inferiore con i pulsanti ecc...
    val plgBtn = new Button("Plague")
    val wrldBtn = new Button("World")
    // potenzialmente da aggiungere le info di diffusione
//  val info = ???

    val bot = new HBox {
      children = Seq(plgBtn, wrldBtn)
    }

    // layout principale
    val mainLayout = new VBox {
      children = Seq(top, bot)
    }

    val mainScene = new Scene {
      fill = Color.rgb(204, 198, 108)
      content = mainLayout
    }

    stage = new JFXApp3.PrimaryStage {
      title = "Plague Sim"
      height = Y
      width = X
      scene = mainScene
    }
  }

}
