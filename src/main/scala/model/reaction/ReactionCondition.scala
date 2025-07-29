package model.reaction

import model.core.SimulationState

/** Trait for defining a condition under which a reaction is triggered.
  */
trait ReactionCondition:
  /** Checks if the condition is satisfied for a given simulation state and
    * node.
    * @param state
    *   the current simulation state
    * @param nodeId
    *   the identifier of the node to check
    * @return
    *   true if the condition is satisfied, false otherwise
    */
  def isSatisfied(state: SimulationState, nodeId: String): Boolean

object ReactionCondition:
  implicit class ConditionOps(condition: ReactionCondition):

    def and(other: ReactionCondition): ReactionCondition =
      (state, nodeId) =>
        condition.isSatisfied(state, nodeId) && other.isSatisfied(state, nodeId)

    def or(other: ReactionCondition): ReactionCondition =
      (state, nodeId) =>
        condition.isSatisfied(state, nodeId) || other.isSatisfied(state, nodeId)

    def unary_! : ReactionCondition =
      (state, nodeId) => !condition.isSatisfied(state, nodeId)

/** Condition that checks if the infected ratio on a node is greater than or
  * equal to a threshold.
  * @param threshold
  *   The minimum infected ratio required to satisfy the condition.
  */
case class InfectedCondition(threshold: Double) extends ReactionCondition:
  /** Returns true if the infected ratio on the node is greater than or equal to
    * the threshold.
    * @param state
    *   The current simulation state.
    * @param nodeId
    *   The node to check.
    * @return
    *   True if the condition is satisfied, false otherwise.
    */
  def isSatisfied(state: SimulationState, nodeId: String): Boolean =
    state.world.nodes
      .get(nodeId)
      .exists(node =>
        node.population > 0 && node.infected.toDouble / node.population >= threshold
      )

/** Condition that checks if the disease severity is greater than or equal to a
  * threshold.
  * @param threshold
  *   The minimum severity required to satisfy the condition.
  */
case class SeverityCondition(threshold: Double) extends ReactionCondition:
  /** Returns true if the disease severity is greater than or equal to the
    * threshold.
    * @param state
    *   The current simulation state.
    * @param nodeId
    *   The node to check (unused).
    * @return
    *   True if the condition is satisfied, false otherwise.
    */
  def isSatisfied(state: SimulationState, nodeId: String): Boolean =
    state.disease.severity >= threshold

/** Condition that checks if both infected ratio and severity thresholds are
  * satisfied.
  * @param infectedThreshold
  *   The minimum infected ratio required.
  * @param severityThreshold
  *   The minimum severity required.
  */
case class InfSeverityCondition(
    infectedThreshold: Double,
    severityThreshold: Double
) extends ReactionCondition:
  /** Returns true if both infected and severity conditions are satisfied.
    * @param state
    *   The current simulation state.
    * @param nodeId
    *   The node to check.
    * @return
    *   True if both conditions are satisfied, false otherwise.
    */
  def isSatisfied(state: SimulationState, nodeId: String): Boolean =
    InfectedCondition(infectedThreshold).isSatisfied(state, nodeId) &&
      SeverityCondition(severityThreshold).isSatisfied(state, nodeId)
