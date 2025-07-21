package model.reaction

import model.time.Time

/** Represents an active reaction in the simulation.
  *
  * @param rule
  *   The reaction rule being applied
  * @param nodeId
  *   The target node ID
  * @param startDay
  *   The day the reaction was triggered
  */
final case class ActiveReaction(
    rule: ReactionRule,
    nodeId: String,
    startDay: Time
):

  private def key: (ReactionRule, String) =
    (rule, nodeId)

  override def equals(obj: Any): Boolean =
    obj match
      case that: ActiveReaction => this.key == that.key
      case _                    => false

  /** Checks if the reaction is still active.
    */
  def isActive(currentDay: Time): Boolean =
    rule.duration match
      case Some(duration) => currentDay < startDay + duration
      case None           => true
