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