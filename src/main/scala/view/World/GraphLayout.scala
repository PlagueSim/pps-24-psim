package view.world

trait GraphLayout:
  /* Computes the 2D positions for a given sequence of node identifiers. */
  def computePositions(nodeIds: Seq[String]): Map[String, (Double, Double)]
