package model.core

import monocle.Lens
import monocle.macros.GenLens

final case class SimulationState(currentDay: Int)

object SimulationState:
  val currentDayLens: Lens[SimulationState, Int] =
    GenLens[SimulationState](_.currentDay)
