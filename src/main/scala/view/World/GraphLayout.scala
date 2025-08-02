package view.world
import model.world.Types.*
trait GraphLayout:
  /* Computes the 2D positions for a given sequence of node identifiers. */
  def computePositions(nodeIds: Seq[NodeId]): Map[NodeId, (PosX, PosY)]
