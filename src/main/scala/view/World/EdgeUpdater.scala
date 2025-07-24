package view.world

import javafx.scene.shape.Line
import model.world.Edge

object EdgeUpdater:

  def update(
              currentEdges: Map[String, Line],
              updatedEdges: Iterable[Edge],
              nodePositions: Map[String, () => (Double, Double)]
            ): (Map[String, Line], Set[Line], Set[Line]) =
    val updatedMap = scala.collection.mutable.Map[String, Line]()
    val toAdd = scala.collection.mutable.Set[Line]()
    val toRemove = currentEdges.values.toSet.to(scala.collection.mutable.Set)

    def edgeId(a: String, b: String): String =
      if a < b then s"$a-$b" else s"$b-$a"

    for edge <- updatedEdges do
      val id = edgeId(edge.nodeA, edge.nodeB)
      val line = currentEdges.get(id) match
        case Some(existing) =>
          val (startX, startY) = nodePositions(edge.nodeA)()
          val (endX, endY) = nodePositions(edge.nodeB)()
          existing.setStartX(startX)
          existing.setStartY(startY)
          existing.setEndX(endX)
          existing.setEndY(endY)
          existing.setStroke(EdgeLayer.edgeColor(edge.typology, edge.isClose))
          toRemove.remove(existing)
          existing
        case None =>
          val newLine = EdgeLayer.createEdgeLine(edge, nodePositions)
          toAdd += newLine
          newLine

      updatedMap(id) = line

    (updatedMap.toMap, toAdd.toSet, toRemove.toSet)
