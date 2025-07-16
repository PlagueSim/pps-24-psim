package model.reaction

import model.core.SimulationState

/**
 * Trait for defining a condition under which a reaction is triggered.
 */
trait ReactionCondition:
    /**
     * Checks if the condition is satisfied for a given simulation state and node.
     * @param state the current simulation state
     * @param nodeId the identifier of the node to check
     * @return true if the condition is satisfied, false otherwise
     */
    def isSatisfied(state: SimulationState, nodeId: String): Boolean
    
/** Simple condition
* when infected on a node are grater than a threshold
* @param threshold the minimum number of infected individuals required to satisfy the condition
*/
case class InfectedCondition(threshold: Double) extends ReactionCondition:
    def isSatisfied(state: SimulationState, nodeId: String): Boolean =
      state.world.nodes.get(nodeId) match
        case Some(node) => node.infected / node.population.toDouble >= threshold
        case None => false