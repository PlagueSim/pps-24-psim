package view.world

trait GraphLayout:
  def computePositions(nodeIds: Seq[String]): Map[String, (Double, Double)]
