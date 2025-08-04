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
  private var _winCondition: SimulationState => Boolean = s =>
    s.world.nodes.map(_._2.population).sum <= 0
  private var _loseCondition: SimulationState => Boolean = s =>
    s.cure.progress >= 1.0
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

  /** Adds a condition that must be met to win the game.
   */
  def addWinCondition(
    winCondition: SimulationState => Boolean
  ): SetupBuilderAndRunner =
    _winCondition = winCondition
    this

  /** Adds a condition that must be met to lose the game.
   */
  def addLoseCondition(
    loseCondition: SimulationState => Boolean
  ): SetupBuilderAndRunner =
    _loseCondition = loseCondition
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
      _winCondition,
      _loseCondition,
      _view,
      _runMode,
      _scheduleMode
    ).run()
