package model.events

import model.core.SimulationState
import model.core.SimulationEngine.Simulation
import model.cure.Cure
import cats.data.State

/** Event responsible for advancing cure research based on global conditions.
 *
 * Follows KISS principle by containing only essential cure advancement logic.
 */
trait CureEvent extends Event[Unit] {
  /** Abstract method to calculate new cure state. */
  def calculateCureProgress(state: SimulationState): Cure

  override def execute(): Simulation[Unit] =
    State.modify { state =>
      val updatedCure = calculateCureProgress(state)
      state.copy(cure = updatedCure)
    }
}

/** Dummy implementation for testing or demonstration. */
case class DummyCureEvent() extends CureEvent {
  override def calculateCureProgress(state: SimulationState): Cure = {
    val day = state.time
    val baseCure = state.cure
    // Every 10th day, add an Additive modifier (without algebraic operations)
    val dayStr = day.toString
    val updatedModifiers =
      if (dayStr.endsWith("0") && dayStr != "0")
        baseCure.modifiers.add(model.cure.CureModifier.Additive(0.01))
      else
        baseCure.modifiers
    // prints current cure progress
    println(s"Current cure progress: ${baseCure.progress * 100}%")
    baseCure.copy(modifiers = updatedModifiers).advance()
  }
}
