package view

trait GraphLayout:
  def computePositions(nodeIds: Seq[String]): Map[String, (Double, Double)]
