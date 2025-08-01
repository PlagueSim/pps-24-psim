package view.util

import scalafx.scene.control.Button

/**
 * A simple standardized button
 */
object StdButton:
  def apply(txt: String)(e: => Unit): Button = new Button:
    text = txt
    minHeight = 50
    minWidth = 150
    onAction = _ => e


