package dsl.builders

import controller.ExecutionMode.{ExecutionMode, TerminalMode}
import model.core.{SimulationEngine, SimulationState}
import model.scheduler.{FixedStandardRateScheduler, Scheduler}
import view.ConsoleSimulationView
import view.updatables.UpdatableView

import scala.annotation.tailrec

class SetupBuilderAndRunner:
  private var _simulationState = SimulationState.createStandardSimulationState()
  private var _conditionsBuilder: SimulationState => Boolean = s => s.time.day.value < 20
  private var _view: UpdatableView = ConsoleSimulationView()
  private var _runMode: ExecutionMode = TerminalMode
  private var _scheduleMode: Scheduler = FixedStandardRateScheduler
  private val _engine = SimulationEngine

  def addSimulationState(state: SimulationState): SetupBuilderAndRunner = 
    _simulationState = state
    this
    
  def addScheduler(scheduler: Scheduler): SetupBuilderAndRunner =
    _scheduleMode = scheduler
    this
    
  def addConditions(conditions: SimulationState => Boolean): SetupBuilderAndRunner =
    _conditionsBuilder = conditions
    this
    
  def setView(bindings: UpdatableView): SetupBuilderAndRunner =
    _view = bindings
    this
  
  def addRun(run: ExecutionMode): SetupBuilderAndRunner =
    _runMode = run
    this
    
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