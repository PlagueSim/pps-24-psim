package model.core

import model.cure.Cure
import model.plague.Disease
import model.time.Time
import monocle.Lens
import monocle.macros.GenLens

final case class SimulationState(time: Time, disease: Disease, cure: Cure)

object SimulationState:
  val currentTimeLens: Lens[SimulationState, Time] =
    GenLens[SimulationState](_.time)

  val currentDiseaseLens: Lens[SimulationState, Disease] =
    GenLens[SimulationState](_.disease)

  val currentCureLens: Lens[SimulationState, Cure] =
    GenLens[SimulationState](_.cure)
