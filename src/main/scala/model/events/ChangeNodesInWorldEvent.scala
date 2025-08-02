package model.events

import model.world.{Node, World}
import model.core.SimulationState
import model.events.Event
import model.world.Types.NodeId

class ChangeNodesInWorldEvent(nodes: Map[NodeId, Node]) extends Event[World]:
  override def modifyFunction(state: SimulationState): World =
    World(nodes, state.world.edges, state.world.movements)