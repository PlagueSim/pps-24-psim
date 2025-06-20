package controller
import view.MainView

class ViewController(val view: MainView) {
  def show(id: String): Unit = id match
    case "PlagueInfo" => view.plgInfo()
    case "WorldInfo" => view.wrldInfo()

}
