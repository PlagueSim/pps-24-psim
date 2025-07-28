package model.events.time

import model.core.SimulationState
import model.events.Event
import model.time.Time

/**
 * Advances the simulation time by one day.
 */
case class AdvanceDayEvent() extends Event[Time]:
  override def modifyFunction(s: SimulationState): Time = s.time + 1
