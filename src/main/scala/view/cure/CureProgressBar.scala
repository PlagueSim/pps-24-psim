package view.cure

import model.core.SimulationState
import scalafx.scene.control.ProgressBar
import view.updatables.UpdatableView

class CureProgressBar extends ProgressBar with UpdatableView:
  progress = 0.0
  prefWidth = 200
  prefHeight = 25

  override def update(newState: SimulationState): Unit =
    progress = newState.cure.progress.min(1.0).max(0.0)
