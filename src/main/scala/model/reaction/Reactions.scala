package model.reaction

import model.reaction.ReactionAction.CloseEdges
import model.time.Time
import model.world.EdgeType

/** Centralized container for reaction state and rules in the simulation.
  *
  * @param rules
  *   List of reaction rules to be checked and triggered.
  * @param activeReactions
  *   Set of currently active reactions.
  */
final case class Reactions(
    rules: List[ReactionRule] = Nil,
    activeReactions: Set[ActiveReaction] = Set.empty
):
  /** Unique key for identifying a reaction by rule and node. */
  private def reactionKey(
      rule: ReactionRule,
      nodeId: String
  ): (ReactionRule, String) =
    (rule, nodeId)

  /** Adds new active reactions to the container.
    * @param newReactions
    *   Set of new active reactions to be added.
    * @return
    *   A new Reactions instance with the added active reactions.
    */
  def addActive(newReactions: Set[ActiveReaction]): Reactions =
    this.copy(activeReactions = activeReactions ++ newReactions)

  /** Removes all expired reactions based on the current day.
    * @param currentDay
    *   The current simulation day.
    * @return
    *   A new Reactions instance with expired reactions removed.
    */
  def removeExpired(currentDay: Time): Reactions =
    remove(expired(currentDay))

  /** Returns the set of expired reactions for the given day.
    * @param currentDay
    *   The current simulation day.
    * @return
    *   Set of expired ActiveReaction instances.
    */
  def expired(currentDay: Time): Set[ActiveReaction] =
    activeReactions.filterNot(_.isActive(currentDay))

  /** Removes the specified set of reactions from the container.
    * @param set
    *   Set of ActiveReaction instances to remove.
    * @return
    *   A new Reactions instance with the specified reactions removed.
    */
  def remove(set: Set[ActiveReaction]): Reactions =
    this.copy(activeReactions = activeReactions.diff(set))

/** Provides standard reaction rules for the simulation.
  */
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
