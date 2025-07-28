package view.updatables

import model.core.SimulationState

/**
 * Represents a view that will be updated with the new information from the simulation state.
 * This trait is used to define views that need to reflect changes in the simulation state.
 */
trait UpdatableView:
    /**
     * Updates the view component with the new simulation state information.
     */
  def update(newState: SimulationState): Unit
