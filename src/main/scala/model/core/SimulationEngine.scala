package model.core

import cats.data.State
import cats.syntax.all.*
import model.events.{AdvanceDayEvent, Event}

object SimulationEngine:
  type Simulation[A] = State[SimulationState, A]

//  def advanceDay(): Simulation[Int] =
//    State
//      .modify[SimulationState](state => SimulationState(state.currentDay + 1))
//      .inspect(x => x.currentDay)
//
//  def advanceFor(days: Int): Simulation[Int] =
//    List.fill(days)(advanceDay()).sequence.map(_.lastOption.getOrElse(-1))
//    (1 to days).foldLeft(State.inspect[SimulationState, Int](_.currentDay)) {
//      (acc, _) => acc.flatMap(_ => advanceDay())
//    }
//    days match
//    case 0 => State.inspect(_.currentDay)
//    case n if n > 0 =>
//      advanceDay().flatMap(_ => advanceFor(n - 1))

  def executeEvent[A](event: Event[A]): Simulation[A] = event.execute()

  def executeListOfEvents[A](events: List[Event[A]]): Simulation[List[A]] =
    events.traverse(executeEvent)

  def runStandardSimulation(): Unit =
    val listOfEvents =
      List(AdvanceDayEvent(), AdvanceDayEvent(), AdvanceDayEvent())
    val initialState           = SimulationState(0)
    val cycle: Simulation[Int] = for
      _   <- executeEvent(AdvanceDayEvent())
      _   <- executeEvent(AdvanceDayEvent())
      _   <- executeListOfEvents(listOfEvents)
      end <- executeEvent(AdvanceDayEvent())
    yield end

    val endSim = cycle.runA(initialState).value
    println(s"Simulation ended on day: $endSim")
