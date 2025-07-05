package model.core

import monocle.Lens
import monocle.macros.GenLens
import model.time.Time

final case class SimulationState(time: Time)

object SimulationState:
  val currentTimeLens: Lens[SimulationState, Time] =
    GenLens[SimulationState](_.time)
