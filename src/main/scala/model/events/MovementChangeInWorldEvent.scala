package model.events

import model.world.{Node, World}
import model.core.SimulationState
import model.events.Event

class MovementChangeInWorldEvent(nodes: Map[String, Node]) extends Event[World]:
  override def modifyFunction(state: SimulationState): World =
    World(
      nodes = nodes,
      edges = state.world.edges,
      movements = state.world.movements
    )