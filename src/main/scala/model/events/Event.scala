package model.events

import cats.data.State
import model.core.SimulationEngine.Simulation

/** Represents a stateful event in the simulation.
  *
  * @tparam A
  *   The type of result produced when the event is executed.
  */
trait Event[A]:
  /** Returns a stateful computation that modifies the simulation state and
    * produces a value of type A.
    */
  def execute(): Simulation[A]

/** Event that advances the simulation by one day.
  *
  * Increments the current day in the simulation state and returns the updated
  * day as an [[Int]].
  */
case class AdvanceDayEvent() extends Event[Int]:
  override def execute(): Simulation[Int] =
    State { s =>
      val updatedState = s.copy(currentDay = s.currentDay + 1)
      updatedState -> updatedState.currentDay
    }
