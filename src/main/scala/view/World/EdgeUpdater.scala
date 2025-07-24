package view.world

import javafx.scene.shape.Line
import model.world.Edge
import model.world.EdgeExtensions.*

object EdgeUpdater:
  /**
   * Updates the visual representation of edges in the scene.
   *
   * It compares the current visual edges (`currentEdges`) with the updated logical edges (`updatedEdges`)
   * and returns:
   *  - a new map of edge IDs to Line instances (both updated and reused),
   *  - the set of new Line instances to be added to the scene,
   *  - the set of old Line instances to be removed from the scene.
   * */
  def update(
              currentEdges: Map[String, Line],
              updatedEdges: Iterable[Edge],
              nodePositions: Map[String, LivePosition]
            ): (Map[String, Line], Set[Line], Set[Line]) =
    val updatedMap = scala.collection.mutable.Map[String, Line]()
    val toAdd = scala.collection.mutable.Set[Line]()
    val toRemove = currentEdges.values.toSet.to(scala.collection.mutable.Set)

    for edge <- updatedEdges do
      val id = edge.edgeId
      val line = currentEdges.get(id) match
        case Some(existing) =>
          val (startX, startY) = nodePositions(edge.nodeA).get()
          val (endX, endY) = nodePositions(edge.nodeB).get()
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
