package view.world

import javafx.scene.shape.Line
import model.world.Edge
import model.world.EdgeExtensions.*
import view.world.EdgeLayer.updateLine
import view.world.EdgeLayer.createEdgeLineSafe

object EdgeUpdater:
  /**
   * @param currentEdges A map of current edges in the scene, where keys are edge IDs and values are Line instances.
   * @param updatedEdges An iterable of updated edges, which may include new edges or modifications to existing ones.
   * @param nodePositions A map of node positions, where keys are node IDs and values are their positions.
   * 
   * @return A tuple containing:
   *         - A map of updated edges with their corresponding Line instances.
   *         - A set of new Line instances to be added to the scene.
   *         - A set of old Line instances to be removed from the scene.
   * This method ensures that the visual representation of edges is kept in sync with the logical state of the world.
   * */

  def update(
              currentEdges: Map[String, Line],
              updatedEdges: Iterable[Edge],
              nodePositions: Map[String, LivePosition]
            ): (Map[String, Line], Set[Line], Set[Line]) = {

    val updatedMap = scala.collection.mutable.Map[String, Line]()
    val toAdd = scala.collection.mutable.Set[Line]()
    val seenIds = scala.collection.mutable.Set[String]()

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
