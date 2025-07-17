package controller

import controller.ExecutionMode.ExecutionMode
import model.core.{SimulationEngine, SimulationState}
import model.scheduler.Scheduler
import view.updatables.UpdatableView

import scala.annotation.tailrec

trait SimulationBinder:
  def bind(
      engine: SimulationEngine.type,
      view: UpdatableView
  ): SimulationStateSetter

trait SimulationStateSetter:
  def withInitialState(state: SimulationState): SimulationEnd

trait SimulationEnd:
  def runUntil(condition: SimulationState => Boolean): SimulationTick

trait SimulationTick:
  def scheduleWith(scheduler: Scheduler): SimulationRunner

trait SimulationRunner:
  def run(mode: ExecutionMode): Unit

object SimulationBinderImpl extends SimulationBinder:
  override def bind(
      engine: SimulationEngine.type,
      view: UpdatableView
  ): SimulationStateSetter = SimulationStateBuilderImpl(
    engine,
    view
  )
  

private case class SimulationStateBuilderImpl(
    engine: SimulationEngine.type,
    view: UpdatableView
) extends SimulationStateSetter:
  override def withInitialState(state: SimulationState): SimulationEnd =
    SimulationEndImpl(engine, view, state)

private case class SimulationEndImpl(
    engine: SimulationEngine.type,
    view: UpdatableView,
    initialState: SimulationState
) extends SimulationEnd:
  override def runUntil(
      condition: SimulationState => Boolean
  ): SimulationTick =
    SimulationTickImpl(engine, view, initialState, condition)

private case class SimulationTickImpl(
    engine: SimulationEngine.type,
    view: UpdatableView,
    state: SimulationState,
    condition: SimulationState => Boolean
) extends SimulationTick:
  override def scheduleWith(scheduler: Scheduler): SimulationRunner =
    SimulationRunnerImpl(engine, view, state, condition, scheduler)

private case class SimulationRunnerImpl(
    engine: SimulationEngine.type,
    view: UpdatableView,
    state: SimulationState,
    condition: SimulationState => Boolean,
    scheduler: Scheduler
) extends SimulationRunner:
  override def run(mode: ExecutionMode): Unit =
    mode.execute {
      mode.runLater(() => view.update(state))
      loop(state, mode.runLater)
    }

  @tailrec
  private def loop(
      simState: SimulationState,
      runLater: Runnable => Unit
  ): SimulationState =
    scheduler.waitForNextTick()
    val nextState = computeNextState(simState)
    computeViewUpdates(nextState, runLater)
    if condition(nextState) then loop(nextState, runLater)
    else nextState

  private def computeNextState(simState: SimulationState): SimulationState =
    engine.runStandardSimulation(simState)

  private def computeViewUpdates(
      simState: SimulationState,
      runLater: Runnable => Unit
  ): Unit =
    runLater(() => view.update(simState))
