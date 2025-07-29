package model.events.reactionsEvents

import model.core.SimulationState
import model.events.Event
import model.world.World
import model.reaction.ActiveReaction

/** Event that applies all active reactions to the world in the simulation
  * state.
  */
case class ApplyReactionsEvent() extends Event[World]:
  /** Applies all active reactions to the world in sequence.
    * @param state
    *   The current simulation state.
    * @return
    *   The updated world after applying all active reactions.
    */
  override def modifyFunction(state: SimulationState): World =
    applyActiveReactions(state.world, state.reactions.activeReactions)

  /** Applies all active reactions to the world in sequence.
    * @param world
    *   The current world state.
    * @param activeReactions
    *   The set of active reactions to apply.
    * @return
    *   The updated world after applying all reactions.
    */
  private def applyActiveReactions(
      world: World,
      activeReactions: Set[ActiveReaction]
  ): World =
    activeReactions.foldLeft(world) { (w, ar) =>
      ar.rule.actionFactory(ar.nodeId).apply(w)
    }
