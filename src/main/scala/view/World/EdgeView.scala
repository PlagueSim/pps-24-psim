package view.world

import scalafx.scene.paint.Color

class EdgeView(private val line: javafx.scene.shape.Line):
  def getLine: javafx.scene.shape.Line = line

  def updateLine(start: (Double, Double), end: (Double, Double)): Unit =
    line.setStartX(start._1)
    line.setStartY(start._2)
    line.setEndX(end._1)
    line.setEndY(end._2)

  def setColor(color: Color): Unit =
    line.setStroke(color)

