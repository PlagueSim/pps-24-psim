package view.world

import javafx.scene.shape.Line
import model.world.Edge
import model.world.EdgeExtensions.*
import view.world.EdgeLayer.{updateLine, createEdgeLineSafe}
import model.world.Types.*
object EdgeUpdater:

  /**
   * Updates the visual representation of edges based on the new state.
   *
   * @param currentEdges Current edge lines in the scene.
   * @param updatedEdges Updated edge data from the model.
   * @param nodePositions Node positions for drawing lines.
   * @return (updated edge map, new lines to add, old lines to remove)
   */
  def update(
              currentEdges: Map[EdgeId, Line],
              updatedEdges: Iterable[Edge],
              nodePositions: Map[NodeId, LivePosition]
            ): (Map[EdgeId, Line], Set[Line], Set[Line]) =

    val (updatedMap, toAdd, seenIds) = updatedEdges.foldLeft((Map.empty[String, Line], Set.empty[Line], Set.empty[String])) {
      case ((accMap, accAdd, accSeen), edge) =>
        val id = edge.edgeId
        val (lineOpt, newLines) = updateOrCreateLine(edge, currentEdges, nodePositions)
        lineOpt match
          case Some(line) => (accMap + (id -> line), accAdd ++ newLines, accSeen + id)
          case None       => (accMap, accAdd, accSeen + id)
    }

    val toRemove = currentEdges.filterNot((id, _) => seenIds.contains(id)).values.toSet

    (updatedMap, toAdd, toRemove)

  private def updateOrCreateLine(
                                  edge: Edge,
                                  currentEdges: Map[EdgeId, Line],
                                  nodePositions: Map[NodeId, LivePosition]
                                ): (Option[Line], Set[Line]) =
    currentEdges.get(edge.edgeId) match
      case Some(existing) =>
        updateLine(existing, edge, nodePositions)
        (Some(existing), Set.empty)

      case None =>
        createEdgeLineSafe(edge, nodePositions) match
          case Some(newLine) => (Some(newLine), Set(newLine))
          case None          => (None, Set.empty)
