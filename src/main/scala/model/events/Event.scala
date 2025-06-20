package model.events

import cats.data.State
import model.core.SimulationEngine.Simulation

trait Event[A] {
  def execute(): Simulation[A]
}

case class AdvanceDayEvent() extends Event[Int]:
  override def execute(): Simulation[Int] =
    State { s =>
      val updatedState = s.copy(currentDay = s.currentDay + 1)
      updatedState -> updatedState.currentDay
    }
