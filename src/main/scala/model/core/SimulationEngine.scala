package model.core

import cats.data.State
import cats.syntax.all._

object SimulationEngine:
  private type Simulation[A] = State[SimulationState, A]

  def advanceDay(): Simulation[Int] =
    State
      .modify[SimulationState](state => SimulationState(state.currentDay + 1))
      .inspect(x => x.currentDay)

  def advanceFor(days: Int): Simulation[Int] =
    List.fill(days)(advanceDay()).sequence.map(_.lastOption.getOrElse(-1))
//    (1 to days).foldLeft(State.inspect[SimulationState, Int](_.currentDay)) {
//      (acc, _) => acc.flatMap(_ => advanceDay())
//    }
//    days match
//    case 0 => State.inspect(_.currentDay)
//    case n if n > 0 =>
//      advanceDay().flatMap(_ => advanceFor(n - 1))
