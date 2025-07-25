package view.world

import scala.math.*

/* A layout strategy that arranges nodes evenly around a circle. */

class CircularLayout(
                      radius: Double = 200,
                      centerX: Double = 400,
                      centerY: Double = 250
                    ) extends GraphLayout:

  /*
   * Computes positions for the given node IDs, placing them evenly
   * spaced around a circle centered at (centerX, centerY).
   * */
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
