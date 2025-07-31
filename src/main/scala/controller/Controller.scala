package controller

import model.core.{SimulationEngine, SimulationState}
import view.updatables.UpdatableView

import scala.annotation.tailrec

class Controller(
    simulationState: model.core.SimulationState,
    conditionsBuilder: model.core.SimulationState => Boolean,
    view: UpdatableView,
    runMode: ExecutionMode.ExecutionMode,
    scheduleMode: model.scheduler.Scheduler
):
  
  private val engine = SimulationEngine

  /** Runs the simulation with the configured settings.
    */
  def run(): Unit =
    runMode.execute {
      runMode.runLater(() => view.update(simulationState))
      loop(simulationState, runMode.runLater)
    }

  @tailrec
  private def loop(
      simState: SimulationState,
      runLater: Runnable => Unit
  ): SimulationState =
    scheduleMode.waitForNextTick()
    val nextState = computeNextState(simState)
    computeViewUpdates(nextState, runLater)
    if conditionsBuilder(nextState) then loop(nextState, runLater)
    else nextState

  private def computeNextState(simState: SimulationState): SimulationState =
    engine.runStandardSimulation(simState)

  private def computeViewUpdates(
      simState: SimulationState,
      runLater: Runnable => Unit
  ): Unit =
    runLater(() => view.update(simState))
