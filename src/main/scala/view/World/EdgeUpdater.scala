package view.world

import javafx.scene.shape.Line
import model.world.Edge
import model.world.EdgeExtensions.*
import view.world.EdgeLayer.updateLine
import view.world.EdgeLayer.createEdgeLineSafe

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
            ): (Map[String, Line], Set[Line], Set[Line]) = {

    val updatedMap = scala.collection.mutable.Map[String, Line]()
    val toAdd = scala.collection.mutable.Set[Line]()
    val seenIds = scala.collection.mutable.Set[String]()

    println("currentEdges: " + currentEdges.keySet)
    println("updatedEdges: " + updatedEdges.map(_.edgeId).toSet)
    for edge <- updatedEdges do
      val id = edge.edgeId
      seenIds += id
      val line = currentEdges.get(id) match
        case Some(existing) =>
          EdgeLayer.updateLine(existing, edge, nodePositions)
          existing
        case None =>
          EdgeLayer.createEdgeLineSafe(edge, nodePositions) match
            case Some(newLine) =>
              toAdd += newLine
              newLine
            case None => null

      if line != null then updatedMap(id) = line

    val toRemove = currentEdges.filterNot { case (id, _) => seenIds.contains(id) }.values.toSet

    (updatedMap.toMap, toAdd.toSet, toRemove)
  }
