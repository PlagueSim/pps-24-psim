package model.core

import cats.data.State
import cats.syntax.all.*
import model.events.{AdvanceDayEvent, Event}

/** Provides the core simulation logic based on the State monad.
  */
object SimulationEngine:

  /** Type alias for a stateful simulation computation. A `Simulation[A]`
    * transforms a SimulationState and returns a value of type A.
    */
  type Simulation[A] = State[SimulationState, A]

  /** Executes a single event and returns the resulting simulation computation.
    *
    * @param event
    *   The event to execute.
    * @tparam A
    *   The type of value produced by the event.
    * @return
    *   A stateful computation that applies the event to the simulation state.
    */
  def executeEvent[A](event: Event[A]): Simulation[A] = event.execute()

  /** Executes a sequence of events in order, returning the results as a list.
    *
    * @param events
    *   The list of events to execute.
    * @tparam A
    *   The result type of each event.
    * @return
    *   A simulation that applies all events and returns a list of their
    *   results.
    */
  def executeListOfEvents[A](
      events: List[Event[A]]
  ): Simulation[List[A]] = // da rivedere
    events.traverse(executeEvent)

  /** Runs a standard simulation scenario for demonstration. It executes several
    * AdvanceDay events and prints the final simulation day.
    */
  def runStandardSimulation(): Unit =
    val listOfEvents =
      List(AdvanceDayEvent(), AdvanceDayEvent(), AdvanceDayEvent())

    val initialState = SimulationState(0)

    val cycle: Simulation[Int] = for
      _   <- executeEvent(AdvanceDayEvent())
      _   <- executeEvent(AdvanceDayEvent())
      _   <- executeListOfEvents(listOfEvents)
      end <- executeEvent(AdvanceDayEvent())
    yield end

    val endSim = cycle.runA(initialState).value
    println(s"Simulation ended on day: $endSim")
