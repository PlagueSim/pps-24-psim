package model.events.reactionsEvents

import model.core.SimulationState
import model.events.Event
import model.reaction.{Reactions, ActiveReaction, ReactionRule}

/** Event that updates the active reactions in the simulation state.
  *
  *   - Removes expired reactions
  *   - Activates new rules if their condition is met
  */
class UpdateActiveReactionsEvent extends Event[Reactions]:
  /** Updates the active reactions by removing expired ones and activating new
    * ones.
    * @param state
    *   The current simulation state.
    * @return
    *   The updated Reactions instance.
    */
  override def modifyFunction(state: SimulationState): Reactions =
    val currentDay              = state.time
    val reactions               = state.reactions
    val reactionsWithoutExpired = removeExpiredReactions(reactions, currentDay)
    activateNewReactions(reactionsWithoutExpired, state)

  /** Removes expired reactions from the given Reactions.
    * @param reactions
    *   The current Reactions instance.
    * @param currentDay
    *   The current simulation day.
    * @return
    *   A new Reactions instance with expired reactions removed.
    */
  private def removeExpiredReactions(
      reactions: Reactions,
      currentDay: model.time.Time
  ): Reactions =
    reactions.removeExpired(currentDay)

  /** Activates new reactions for rules whose condition is met.
    * @param reactions
    *   The current Reactions instance.
    * @param state
    *   The current simulation state.
    * @return
    *   A new Reactions instance with new active reactions added.
    */
  private def activateNewReactions(
      reactions: Reactions,
      state: SimulationState
  ): Reactions =
    val newActiveReactions =
      reactions.rules.flatMap(rule => activateReaction(rule, state)).toSet
    reactions.addActive(newActiveReactions)

  /** Creates active reactions for a rule if its condition is met for any node.
    * @param rule
    *   The reaction rule to check.
    * @param state
    *   The current simulation state.
    * @return
    *   Set of new ActiveReaction instances.
    */
  private def activateReaction(
      rule: ReactionRule,
      state: SimulationState
  ): Set[ActiveReaction] =
    state.world.nodes.keys.collect {
      case nodeId if rule.shouldTrigger(state, nodeId) =>
        ActiveReaction(rule, nodeId, state.time)
    }.toSet
