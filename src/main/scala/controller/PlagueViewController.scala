package controller

import scalafx.scene.Node
import view.PlagueView

class PlagueViewController(val view: PlagueView):
  def show(showable: Node): Unit = view.center = showable

