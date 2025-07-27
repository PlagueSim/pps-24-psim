package model.reaction

import model.reaction.ReactionAction.CloseEdges
import model.time.Time
import model.world.EdgeType

/** Centralized container for reaction state and rules */
final case class Reactions(
    rules: List[ReactionRule] = Nil,
    activeReactions: Set[ActiveReaction] = Set.empty
):
  
  private def reactionKey(rule: ReactionRule, nodeId: String): (ReactionRule, String) =
    (rule, nodeId)
  /** Adds new active reactions
    * @param newReactions
    *   List of new active reactions to be added
    * @return
    *   A new Reactions instance with the added active reactions
    */
  def addActive(newReactions: Set[ActiveReaction]): Reactions =
    this.copy(activeReactions = activeReactions ++ newReactions)

  def removeExpired(currentDay: Time): Reactions =
    remove(expired(currentDay))
    
  def expired(currentDay: Time): Set[ActiveReaction] =
    activeReactions.filterNot(_.isActive(currentDay))
    
  def remove(set: Set[ActiveReaction]): Reactions =
    this.copy(activeReactions = activeReactions.filterNot(set.contains))
    
object Reactions:
  val StandardReactions: Reactions =
    Reactions(
      rules = List(
        ReactionRule(
          condition = InfSeverityCondition(
            infectedThreshold = 0.3,
            severityThreshold = 3
          ),
          actionFactory = nodeId => CloseEdges(EdgeType.Land, nodeId)
        )
      )
    )