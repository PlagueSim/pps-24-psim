package model.events

import model.core.SimulationState
import model.core.SimulationEngine.Simulation
import model.cure.Cure
import cats.data.State

/** Event that only execute the advance of the cure progress.
  *
  * This event modifies the simulation state by advancing the cure's progress
  * based on the current day and applying any modifiers that may be present.
  */
case class BasicCureEvent() extends Event[Cure]:
  override def modifyFunction(state: SimulationState): Cure =
    state.cure.advance()
