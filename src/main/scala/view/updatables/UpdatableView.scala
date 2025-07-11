package view.updatables

import model.core.SimulationState

trait UpdatableView:
  def update(newState: SimulationState): Unit
