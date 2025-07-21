package model.reaction

import model.core.SimulationState

final case class ReactionRule(
    condition: ReactionCondition,
    actionFactory: String => ReactionAction, // Changed to factory function
    duration: Option[Int] = None
):
  def shouldTrigger(state: SimulationState, nodeId: String): Boolean =
    condition.isSatisfied(state, nodeId)
