package controller

import model.core.{SimulationEngine, SimulationState}
import model.scheduler.Scheduler
import view.updatables.UpdatableView

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

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
  def run(runLater: Runnable => Unit, isGuiThread: Boolean)(using
      execContext: ExecutionContext
  ): Unit

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

  override def run(
      runLater: Runnable => Unit,
      isGuiThread: Boolean
  )(using execContext: ExecutionContext): Unit =
    if isGuiThread then
      Future {
        runLater(() => view.update(state))
        loop(state, runLater)
      }
    else
      runLater(() => view.update(state))
      loop(state, runLater)

  @tailrec
  private final def loop(
      simState: SimulationState,
      runLater: Runnable => Unit
  ): SimulationState =
    scheduler.waitForNextTick()
    val nextState = computeNextStateAndUpdateView(simState, runLater)
    if condition.apply(nextState) then loop(nextState, runLater)
    else nextState

  private def computeNextStateAndUpdateView(
      simState: SimulationState,
      runLater: Runnable => Unit
  ): SimulationState =
    val nextState = engine.runStandardSimulation(simState)
    runLater(() => view.update(nextState))
    nextState
