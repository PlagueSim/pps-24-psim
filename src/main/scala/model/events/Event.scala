package model.events

import cats.data.State
import model.core.SimulationEngine.Simulation
import model.core.SimulationState

trait Event[A] {
  def execute(): Simulation[A]
}

case class AdvanceDayEvent() extends Event[Int] {
  override def execute(): Simulation[Int] =
    State
      .modify[SimulationState](s => SimulationState(s.currentDay + 1))
      .inspect(_.currentDay)
}
