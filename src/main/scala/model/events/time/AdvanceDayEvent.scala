package model.events.time

import model.core.SimulationState
import model.events.Event
import model.time.Time

case class AdvanceDayEvent() extends Event[Time]:
  override def modifyFunction(s: SimulationState): Time = s.time + 1
