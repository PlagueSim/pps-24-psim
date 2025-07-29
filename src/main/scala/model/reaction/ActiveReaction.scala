package model.reaction

import model.time.Time

/** Represents an active reaction applied to a node in the simulation.
  *
  * Equality is based only on the reaction rule and node ID, ignoring the start
  * day.
  *
  * @param rule
  *   The reaction rule being applied.
  * @param nodeId
  *   The target node ID.
  * @param startDay
  *   The day the reaction was triggered.
  */
final case class ActiveReaction(
    rule: ReactionRule,
    nodeId: String,
    startDay: Time
):

  /** Unique key for equality and hashCode, based on rule and nodeId. */
  private def key: (ReactionRule, String) = (rule, nodeId)

  override def equals(obj: Any): Boolean =
    obj match {
      case that: ActiveReaction => this.key == that.key
      case _                    => false
    }

  override def hashCode(): Int = key.hashCode()

  /** Checks if the reaction is still active at the given day.
    *
    * @param currentDay
    *   The current simulation day.
    * @return
    *   True if the reaction is still active, false otherwise.
    */
  def isActive(currentDay: Time): Boolean =
    rule.duration.forall(duration => currentDay < startDay + duration)
