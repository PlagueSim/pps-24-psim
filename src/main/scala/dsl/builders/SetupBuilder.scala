package dsl.builders

import model.core.{SimulationEngine, SimulationState}
import view.updatables.UpdatableView
import controller.ExecutionMode.{ExecutionMode, GuiFXMode}
import model.scheduler.{FixedStandardRateScheduler, Scheduler}
import view.MainView

import scala.annotation.tailrec

class SetupBuilder:
  private var _simulationState = SimulationState.createStandardSimulationState()
  private var _conditionsBuilder: SimulationState => Boolean = s => s.time.day.value < 20
  private var _view: UpdatableView = MainView()
  private var _runMode: ExecutionMode = GuiFXMode
  private var _scheduleMode: Scheduler = FixedStandardRateScheduler
  private val _engine = SimulationEngine

  def addSimulationState(state: SimulationState): SetupBuilder = 
    _simulationState = state
    this
    
  def addScheduler(scheduler: Scheduler): SetupBuilder =
    _scheduleMode = scheduler
    this
    
  def addConditions(conditions: SimulationState => Boolean): SetupBuilder =
    _conditionsBuilder = conditions
    this
    
  def setView(bindings: UpdatableView): SetupBuilder =
    _view = bindings
    this
  
  def addRun(run: ExecutionMode): SetupBuilder =
    _runMode = run
    this
    
  def build(): Unit =
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