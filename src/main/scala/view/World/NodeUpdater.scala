package view.world

import model.world.Node
import javafx.scene.Node as FxNode

object NodeUpdater:

  def update(
              currentNodes: Map[String, NodeView],
              updatedNodes: Map[String, Node]
            ): (Map[String, NodeView], Set[FxNode], Set[FxNode]) =
    val updatedMap = scala.collection.mutable.Map[String, NodeView]()
    val toAdd = scala.collection.mutable.Set[FxNode]()
    val toRemove = scala.collection.mutable.Set[FxNode]()

    for (id, updatedNode) <- updatedNodes do
      currentNodes.get(id) match
        case Some(existingView) =>
          val updatedView = existingView.withUpdatedLabelsFromModel(updatedNode)
          updatedMap(id) = updatedView
        case None =>
          val factory = new DefaultNodeViewFactory(() => {})
          val newView = factory.createNode(id, updatedNode, (0.0, 0.0)) // fallback pos
          updatedMap(id) = newView
          toAdd ++= newView.visuals

    for (id, oldView) <- currentNodes if !updatedNodes.contains(id) do
      toRemove ++= oldView.visuals

    (updatedMap.toMap, toAdd.toSet, toRemove.toSet)
