package model.events

import cats.data.State
import model.core.SimulationEngine.Simulation
import model.core.SimulationState
import model.time.Time

/** Represents a stateful event in the simulation.
  *
  * @tparam A
  *   The type of result produced when the event is executed.
  */
trait Event[A]:
  /** Returns a stateful computation that modifies the simulation state and
    * produces a value of type A.
    */
  def execute(): Simulation[A] =
    for
      s <- State.get[SimulationState]
      newFieldValue     = modifyFunction(s)
      updatedState      = s.replace(newFieldValue)
      _ <- State.set(updatedState)
    yield newFieldValue

  def modifyFunction(state: SimulationState): A

case class AdvanceDayEvent() extends Event[Time]:
  override def modifyFunction(s: SimulationState): Time = s.time + 1

