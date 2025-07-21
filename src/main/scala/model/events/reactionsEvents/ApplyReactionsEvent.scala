package model.events.reactionsEvents

import model.core.SimulationState
import model.events.Event
import model.world.World
import model.reaction.ActiveReaction

case class ApplyReactionsEvent() extends Event[World]:
  override def modifyFunction(state: SimulationState): World =
    applyActiveReactions(state.world, state.reactions.activeReactions)

  /** Applies all active reactions to the world in sequence. */
  private def applyActiveReactions(
      world: World,
      activeReactions: Set[ActiveReaction]
  ): World =
    activeReactions.foldLeft(world) { (w, ar) =>
      ar.rule.actionFactory(ar.nodeId).apply(w)
    }
