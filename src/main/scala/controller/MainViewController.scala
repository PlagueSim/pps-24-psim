package controller
import view.MainView

class MainViewController(val view: MainView):
  def show(id: String): Unit = view.show(id)

