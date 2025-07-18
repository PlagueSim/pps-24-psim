package model.reaction

import model.core.SimulationState

final case class Reaction(
    condition: ReactionCondition,
    action: ReactionAction,
    duration: Option[Int] = None
):

  /** Checks if the reaction should be triggered for a node
    */
  def shouldTrigger(state: SimulationState, nodeId: String): Boolean =
    condition.isSatisfied(state, nodeId)
