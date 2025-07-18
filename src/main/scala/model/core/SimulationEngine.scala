package model.core

import cats.data.State
import cats.syntax.all.*
import model.world.{MovementStrategy, Static, World}
import model.cure.Cure
import model.events.DiseaseEvents.Mutation
import model.events.movementEvent.MovementEvent
import model.events.{AdvanceDayEvent, BasicCureEvent, Event, MovementChangeInWorldEvent}
import model.plague.Disease
import model.time.BasicYear
import model.time.TimeTypes.*

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
  def runSim(): Unit =
    val listOfEvents =
      List(AdvanceDayEvent(), AdvanceDayEvent(), AdvanceDayEvent())

    val movements: Map[MovementStrategy, Double] = Map(
      Static -> 1.0
    )

    val initialState = SimulationState(
      BasicYear(Day(0), Year(2023)),
      Disease("a", Set.empty, 0),
      Cure(),
      World(Map.empty, Set.empty, movements)
    )
    val endSim = simulationLoop().runS(initialState).value.time.day.value
    println(s"Simulation ended on day: $endSim")

  private def simulationLoop(): Simulation[Unit] = for
    time <- executeEvent(AdvanceDayEvent())
    _    <- if time.day.value < 6 then simulationLoop() else State.pure(())
  yield ()

  def runStandardSimulation(state: SimulationState): SimulationState =
    val tick =
      for
        moves <- executeEvent(MovementEvent())
        _ <- executeEvent(MovementChangeInWorldEvent(moves))
        _ <- executeEvent(BasicCureEvent())
        _ <- executeEvent(AdvanceDayEvent())
        _ <- executeEvent(Mutation())
      yield ()
    tick.runS(state).value

//oggetto intenzione che è un mapper da intenzione dell'entita ad un evento.
//evento chiama la generazione delle intenzioni
//dopo l'interrogazione ho una lista di cose che devo fare, queste le converto in eventi e li eseguo.
