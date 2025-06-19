package model.core

import cats.data.State

object SimulationEngine:
  private type Simulation[A] = State[SimulationState, A]

  def advanceDay(): Simulation[Int] =
    State { simState =>
      val nextDay = simState.currentDay + 1
      (SimulationState(nextDay), nextDay)
    }
