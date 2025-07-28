package model.events

import cats.data.State
import model.core.SimulationEngine.Simulation
import model.core.SimulationState
import model.time.Time

/** Represents a stateful event in the simulation.
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

  /** The function that modifies the simulation state.
   */
  def modifyFunction(state: SimulationState): A
