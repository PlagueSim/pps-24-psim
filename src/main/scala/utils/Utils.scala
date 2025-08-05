package utils

import model.core.SimulationState

/**
 * Utility object containing constants and standard conditions for the simulation.
 */
object Utils:
  val DAY_ZERO: Int = 0
  val YEAR_ZERO: Int = 2025

  val SCHEDULING_STEP: Int = 500

  val DISEASE_POINTS: Int = 100
  val CURE_BASE_SPEED: Double = 0.00
  val CURE_PROGRESS: Double = 0.0

  val MAX_WIDTH: Double = 0.66
  val MAX_HEIGHT: Double = 0.66
  val MIN_WIDTH: Double = 0.33
  val MIN_HEIGHT: Double = 0.33

  /**
   * Standard win condition, the total population of all nodes must be 0.
   */
  val WIN_CONDITION: SimulationState => Boolean = s =>
    s.world.nodes.map(_._2.population).sum <= 0

  /**
   * Standard lose condition, the cure progress must be 1.0 or the total infected population must be 0.
   */
  val LOSE_CONDITION: SimulationState => Boolean = s =>
    s.cure.progress >= 1.0 || s.world.nodes.map(_._2.infected).sum <= 0
