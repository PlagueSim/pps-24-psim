package model.events.reactionsEvents

import model.core.SimulationState
import model.events.Event
import model.world.World

case class RevertExpiredEvent() extends Event[World]:
  override def modifyFunction(state: SimulationState): World =
    val expiredReactions = state.reactions.expired(state.time)
    expiredReactions.foldLeft(state.world) { (world, expired) =>
      val action = expired.rule.actionFactory(expired.nodeId)
      action.reverse(world)
    }
