package model.core

private class SimulationState(val currentDay: Int) {}

object SimulationState:
  def apply(day: Int): SimulationState =
    new SimulationState(day)
