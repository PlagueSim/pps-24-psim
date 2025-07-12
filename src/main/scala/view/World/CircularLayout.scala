package view.world

import scala.math.*

class CircularLayout(
                      radius: Double = 200,
                      centerX: Double = 400,
                      centerY: Double = 250
                    ) extends GraphLayout:
  def computePositions(nodeIds: Seq[String]): Map[String, (Double, Double)] =
    if nodeIds.isEmpty then Map.empty
    else
      val angleStep = 2 * Pi / nodeIds.size
      nodeIds.zipWithIndex.map { (id, i) =>
        val angle = i * angleStep
        val x = centerX + radius * cos(angle)
        val y = centerY + radius * sin(angle)
        id -> (x, y)
      }.toMap
