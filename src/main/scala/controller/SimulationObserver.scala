package controller

import model.core.{SimulationEngine, SimulationState}
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}
import view.updatables.UpdatableView

import scala.annotation.tailrec

trait SimulationObserver:
  def bind(engine: SimulationEngine.type, view: UpdatableView): SimBuilder

private case class SimBuilder(
    engine: SimulationEngine.type,
    view: UpdatableView
):
  def run(runLater: Runnable => Unit): Unit =
    val thread = new Thread(() => {
      val initialState = SimulationState(
        BasicYear(Day(0), Year(2023))
      )
      runLater(() => view.update(initialState))
      val finalState = loop(initialState, runLater)

      println(
        s"Simulation completed with result: ${finalState.time.day.value}, ${finalState.time.year.value}"
      )

    })
    thread.setDaemon(true)
    thread.start()

  @tailrec
  private final def loop(
      simState: SimulationState,
      runLater: Runnable => Unit
  ): SimulationState =
    Thread.sleep(1000)
    val nextState = engine.runStandardSimulation1(simState)
    runLater(() => view.update(nextState))
    if nextState.time.day.value < 10 then loop(nextState, runLater)
    else
      println(
        s"Simulation reached day ${nextState.time.day.value}, stopping."
      )
      nextState

class SimulationObserverImpl extends SimulationObserver:
  override def bind(
      engine: SimulationEngine.type,
      view: UpdatableView
  ): SimBuilder = SimBuilder(
    engine,
    view
  )
