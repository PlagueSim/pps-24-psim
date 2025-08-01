package controller

import controller.ExecutionMode.ExecutionMode
import model.core.{SimulationEngine, SimulationState}
import model.scheduler.Scheduler
import view.updatables.UpdatableView

import scala.annotation.tailrec

/** The main controller for the simulation, responsible for running the
  * simulation loop and updating the view.
  */
class Controller(
    simulationState: SimulationState,
    canRun: SimulationState => Boolean,
    view: UpdatableView,
    runMode: ExecutionMode,
    scheduleMode: Scheduler
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
    if canRun(nextState) then loop(nextState, runLater)
    else nextState

  private def computeNextState(simState: SimulationState): SimulationState =
    engine.runStandardSimulation(simState)

  private def computeViewUpdates(
      simState: SimulationState,
      runLater: Runnable => Unit
  ): Unit =
    runLater(() => view.update(simState))
