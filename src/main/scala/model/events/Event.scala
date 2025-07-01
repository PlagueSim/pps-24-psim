package model.events

import cats.data.State
import model.core.SimulationEngine.Simulation
import model.core.SimulationState
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

//case class AdvanceDayEvent() extends Event[Int]:
//  override def execute(): Simulation[Int] =
//    State { s =>
//      val updatedState = s.copy(currentDay = s.currentDay + 1)
//      updatedState -> updatedState.currentDay
//    }

trait ModifyFieldEvent[T] extends Event[T]:
  val fieldLens: Lens[SimulationState, T]

  def modifyFunction(currentValue: T): T

  final override def execute(): Simulation[T] =
    for
      s <- State.get[SimulationState]
      currentFieldValue = fieldLens.get(s)
      newFieldValue     = modifyFunction(currentFieldValue)
      _ <- State.set(fieldLens.replace(newFieldValue)(s))
    yield newFieldValue

/** Event that advances the simulation by one day.
  *
  * Increments the current day in the simulation state and returns the updated
  * day as an [[Int]].
  */
case class AdvanceDayEvent() extends ModifyFieldEvent[Int]:
  override val fieldLens: Lens[SimulationState, Int] = SimulationState.currentDayLens
  override def modifyFunction(currentDay: Int): Int = currentDay + 1
