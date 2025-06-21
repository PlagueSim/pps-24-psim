package model.logic

import model.core.SimulationState

/**
 * Generic trait for state evolution logic
 * @tparam A Type of the state component to evolve
 */
trait Logic[A] {
	/**
	 * Evolves a specific component of the game state
	 * @param state Current global game state
	 * @return New evolved state of component A
	 */
	def evolve(state: SimulationState): A
}
