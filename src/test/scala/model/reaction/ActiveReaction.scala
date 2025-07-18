package model.reaction

import model.time.Time

extension (t1: Time)
  def <(t2: Time): Boolean =
    if t1.year.value == t2.year.value then t1.day.value < t2.day.value
    else t1.year.value < t2.year.value

  def >(t2: Time): Boolean =
    if t1.year.value == t2.year.value then t1.day.value > t2.day.value
    else t1.year.value > t2.year.value

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
  /** Checks if the reaction is still active.
    */
  def isActive(currentDay: Time): Boolean =
    rule.duration match
      case Some(duration) => currentDay < startDay + duration
      case None           => true
