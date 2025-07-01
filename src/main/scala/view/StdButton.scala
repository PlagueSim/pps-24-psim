package view

import scalafx.scene.control.Button

//might be moved to a factory
object StdButton:
  def apply(txt: String)(e: => Unit): Button = new Button:
    text = txt
    minHeight = 50
    minWidth = 150
    onAction = _ => e


