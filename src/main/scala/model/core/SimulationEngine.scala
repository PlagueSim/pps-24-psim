package model.core

import cats.data.State
import cats.syntax.all.*
import model.cure.Cure
import model.events.plague.DiseaseEvents.*
import model.events.cure.AdvanceCureEvent
import model.events.movementEvent.MovementEvent
import model.events.reactionsEvents.{ApplyReactionsEvent, RevertExpiredEvent, UpdateActiveReactionsEvent}
import model.events.{AdvanceDayEvent, ChangeNodesInWorldEvent, CureEventBuffer, DeathEvent, DiseaseEventBuffer, Event, EventBuffer, InfectionEvent}
import model.time.TimeTypes.*
import model.world.World

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

    /** Runs a standard simulation step, executing a series of predefined events
      * in the correct order.
      *
      * @param state
      *   The current simulation state.
      * @return
      *   The updated simulation state after executing the standard simulation step.
      */
  def runStandardSimulation(state: SimulationState): SimulationState =
    val tick = for
      _     <- executeEvent(DiseaseEventBuffer)
      _     <- executeEvent(CureEventBuffer)
      _     <- executeEvent(RevertExpiredEvent())
      _     <- executeEvent(UpdateActiveReactionsEvent())
      _     <- executeEvent(ApplyReactionsEvent())
      x     <- executeEvent(InfectionEvent())
      _     <- executeEvent(DnaPointsAddition(x))
      _     <- executeEvent(ChangeNodesInWorldEvent(x))
      y     <- executeEvent(DeathEvent())
      _     <- executeEvent(ChangeNodesInWorldEvent(y))
      moves <- executeEvent(MovementEvent())
      _     <- executeEvent(ChangeNodesInWorldEvent(moves))
      _     <- executeEvent(AdvanceCureEvent())
      _     <- executeEvent(Mutation())
      _     <- executeEvent(AdvanceDayEvent())
    yield ()
    tick.runS(state).value
