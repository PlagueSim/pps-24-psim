package model.events.reactionsEvents

import model.core.SimulationState
import model.events.Event
import model.reaction.{Reactions, ActiveReaction, ReactionRule}

/** Event that updates the active reactions:
  *   1. Removes expired reactions
  *   2. Activates new rules if their condition is met
  */
class UpdateActiveReactionsEvent extends Event[Reactions]:
  override def modifyFunction(state: SimulationState): Reactions =
    val currentDay              = state.time
    val reactions               = state.reactions
    val reactionsWithoutExpired = removeExpiredReactions(reactions, currentDay)
    val updatedReactions = activateNewReactions(reactionsWithoutExpired, state)
    updatedReactions

  /** Removes expired reactions from the given Reactions */
  private def removeExpiredReactions(
      reactions: Reactions,
      currentDay: model.time.Time
  ): Reactions =
    reactions.removeExpired(currentDay)

  private def activateReaction(
      rule: ReactionRule,
      state: SimulationState
  ): Set[ActiveReaction] =
    state.world.nodes.keys.collect {
      case nodeId if rule.shouldTrigger(state, nodeId) =>
        ActiveReaction(rule, nodeId, state.time)
    }.toSet

  private def activateNewReactions(
      reactions: Reactions,
      state: SimulationState
  ): Reactions =
    val newActiveReactions =
      reactions.rules.flatMap(rule => activateReaction(rule, state))
    reactions.addActive(newActiveReactions.toSet)
