package model.reaction

import model.core.SimulationState

/** Represents a rule for triggering a reaction in the simulation.
  *
  * @param condition
  *   The condition under which the reaction is triggered.
  * @param actionFactory
  *   A factory function that produces a ReactionAction for a given node ID.
  * @param duration
  *   Optional duration (in days) for which the reaction remains active.
  */
final case class ReactionRule(
    condition: ReactionCondition,
    actionFactory: String => ReactionAction, // Factory function for node-specific actions
    duration: Option[Int] = None
):
  /** Checks if the rule should trigger for the given simulation state and node.
    * @param state
    *   The current simulation state.
    * @param nodeId
    *   The node to check.
    * @return
    *   True if the condition is satisfied, false otherwise.
    */
  def shouldTrigger(state: SimulationState, nodeId: String): Boolean =
    condition.isSatisfied(state, nodeId)
