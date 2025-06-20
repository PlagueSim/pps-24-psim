import scalafx.application.JFXApp3
import scalafx.stage.Screen
import view.MainView

object App extends JFXApp3 {
  override def start(): Unit = {
    val X = Screen.primary.bounds.width
    val Y = Screen.primary.bounds.height

    stage = new JFXApp3.PrimaryStage {
      title = "Plague Sim"
      scene = new MainView
      width = X * 0.66
      height = Y * 0.66
      minWidth = X * 0.33
      minHeight = Y * 0.33
    }
  }
}
