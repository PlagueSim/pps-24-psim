package model.events

import cats.data.State
import model.core.SimulationEngine.Simulation
import model.core.SimulationState
import model.time.Time
import monocle.Lens

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

//case class AdvanceDayEvent() extends Event[Time]:
//  override def execute(): Simulation[Time] =
//    State { s =>
//      val updatedState = s.copy(time = s.time + 1)
//      updatedState -> updatedState.time
//    }

trait ModifyFieldEvent[T] extends Event[T]:
  val valueToBeChanged: Lens[SimulationState, T]

  def modifyFunction(currentValue: T): T

  final override def execute(): Simulation[T] =
    for
      s <- State.get[SimulationState]
      currentFieldValue = valueToBeChanged.get(s)
      newFieldValue     = modifyFunction(currentFieldValue)
      _ <- State.set(valueToBeChanged.replace(newFieldValue)(s))
    yield newFieldValue

/** Event that advances the simulation by one day.
  *
  * Increments the current day in the simulation state and returns the updated
  * day as a [[Time]].
  */
case class AdvanceDayEvent() extends ModifyFieldEvent[Time]:
  override val valueToBeChanged: Lens[SimulationState, Time] =
    SimulationState.currentTimeLens
  override def modifyFunction(currentDay: Time): Time = currentDay + 1
