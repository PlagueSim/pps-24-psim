package dsl.builders

import controller.Controller
import controller.ExecutionMode.{ExecutionMode, TerminalMode}
import model.core.SimulationState
import model.scheduler.{FixedStandardRateScheduler, Scheduler}
import view.ConsoleSimulationView
import view.updatables.UpdatableView

/** A builder class for setting up and running a simulation. It collects all the
  * necessary parts and then starts the simulation.
  */
class SetupBuilderAndRunner:
  private var _simulationState = SimulationState.createStandardSimulationState()
  private var _canRunCondition: SimulationState => Boolean = s =>
    s.time.day.value < 20
  private var _view: UpdatableView     = ConsoleSimulationView()
  private var _runMode: ExecutionMode  = TerminalMode
  private var _scheduleMode: Scheduler = FixedStandardRateScheduler

  /** Adds a simulation state to the builder.
    */
  def addSimulationState(state: SimulationState): SetupBuilderAndRunner =
    _simulationState = state
    this

  /** Adds a scheduler to the simulation.
    */
  def addScheduler(scheduler: Scheduler): SetupBuilderAndRunner =
    _scheduleMode = scheduler
    this

  /** Adds a condition that must be met for the simulation to continue.
    */
  def addConditions(
      conditions: SimulationState => Boolean
  ): SetupBuilderAndRunner =
    _canRunCondition = conditions
    this

  /** Sets the view for the simulation.
    */
  def setView(bindings: UpdatableView): SetupBuilderAndRunner =
    _view = bindings
    this

  /** Sets the execution mode for the simulation.
    */
  def addRun(run: ExecutionMode): SetupBuilderAndRunner =
    _runMode = run
    this

  /** Runs the simulation with the configured settings.
    */
  def buildAndRun(): Unit =
    Controller(
      _simulationState,
      _canRunCondition,
      _view,
      _runMode,
      _scheduleMode
    ).run()
