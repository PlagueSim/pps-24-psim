package model.reaction

import model.time.Time

/** Centralized container for reaction state and rules */
final case class Reactions(
    rules: List[ReactionRule] = Nil,
    activeReactions: List[ActiveReaction] = Nil
):
  /** Adds new active reactions
    * @param newReactions
    *   List of new active reactions to be added
    * @return
    *   A new Reactions instance with the added active reactions
    */
  def addActive(newReactions: List[ActiveReaction]): Reactions =
    this.copy(activeReactions = activeReactions ++ newReactions)

  def removeExpired(currentDay: Time): Reactions =
    remove(expired(currentDay))
    
  def expired(currentDay: Time): List[ActiveReaction] =
    activeReactions.filterNot(_.isActive(currentDay))
    
  def remove(list: List[ActiveReaction]): Reactions =
    this.copy(activeReactions = activeReactions.filterNot(list.contains))