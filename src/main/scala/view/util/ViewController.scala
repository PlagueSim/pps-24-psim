package view.util

import scalafx.scene.Node
import scalafx.scene.layout.BorderPane

class ViewController(val view: BorderPane):
  def show(showable: Node): Unit = view.center = showable

