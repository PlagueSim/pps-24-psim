package dsl.builders

import controller.ExecutionMode.{ExecutionMode, TerminalMode}
import model.core.{SimulationEngine, SimulationState}
import model.scheduler.{FixedStandardRateScheduler, Scheduler}
import view.ConsoleSimulationView
import view.updatables.UpdatableView

import scala.annotation.tailrec

/**
 * A builder class for setting up and running a simulation.
 * It collects all the necessary parts and then starts the simulation.
 */
class SetupBuilderAndRunner:
  private var _simulationState = SimulationState.createStandardSimulationState()
  private var _conditionsBuilder: SimulationState => Boolean = s => s.time.day.value < 20
  private var _view: UpdatableView = ConsoleSimulationView()
  private var _runMode: ExecutionMode = TerminalMode
  private var _scheduleMode: Scheduler = FixedStandardRateScheduler
  private val _engine = SimulationEngine

  /**
   * Adds a simulation state to the builder.
   */
  def addSimulationState(state: SimulationState): SetupBuilderAndRunner =
    _simulationState = state
    this

  /**
   * Adds a scheduler to the simulation.
   */
  def addScheduler(scheduler: Scheduler): SetupBuilderAndRunner =
    _scheduleMode = scheduler
    this

  /**
   * Adds a condition that must be met for the simulation to continue.
   */
  def addConditions(conditions: SimulationState => Boolean): SetupBuilderAndRunner =
    _conditionsBuilder = conditions
    this

  /**
   * Sets the view for the simulation.
   */
  def setView(bindings: UpdatableView): SetupBuilderAndRunner =
    _view = bindings
    this

  /**
   * Sets the execution mode for the simulation.
   */
  def addRun(run: ExecutionMode): SetupBuilderAndRunner =
    _runMode = run
    this

  /**
   * Runs the simulation with the configured settings.
   */
  def run(): Unit =
    _runMode.execute {
      _runMode.runLater(() => _view.update(_simulationState))
      loop(_simulationState, _runMode.runLater)
    }

  @tailrec
  private def loop(
                    simState: SimulationState,
                    runLater: Runnable => Unit
                  ): SimulationState =
    _scheduleMode.waitForNextTick()
    val nextState = computeNextState(simState)
    computeViewUpdates(nextState, runLater)
    if _conditionsBuilder(nextState) then loop(nextState, runLater)
    else nextState

  private def computeNextState(simState: SimulationState): SimulationState =
    _engine.runStandardSimulation(simState)

  private def computeViewUpdates(
                                  simState: SimulationState,
                                  runLater: Runnable => Unit
                                ): Unit =
    runLater(() => _view.update(simState))
