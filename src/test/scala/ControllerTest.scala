import controller.SimulationBinderImpl
import model.core.{SimulationEngine, SimulationState}
import model.cure.Cure
import model.plague.Disease
import model.scheduler.CustomScheduler
import model.time.BasicYear
import model.time.TimeTypes.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import view.updatables.UpdatableView
import model.world.{MovementStrategy, Static, World}

import scala.concurrent.ExecutionContext

class ControllerTest extends AnyFlatSpec with Matchers:

  class TerminalView extends UpdatableView:
    override def update(state: SimulationState): Unit =
      println(
        s"Current Time: ${state.time.day.value} " +
          s"of ${state.time.year.value}"
      )

  val terminalView                             = TerminalView()
  val movements: Map[MovementStrategy, Double] = Map(
    Static -> 1.0
  )
  val initialState: SimulationState = SimulationState(
    BasicYear(Day(0), Year(2023)),
    Disease("a", Set.empty, 0),
    Cure(),
    World(Map.empty, Set.empty, movements)
  )

  "A controller" should "bind the simulation engine to a generic view" in:
    SimulationBinderImpl bind (
      SimulationEngine,
      terminalView
    )

  it should "then request an initial state" in:
    SimulationBinderImpl bind (
      SimulationEngine,
      terminalView
    ) withInitialState initialState

  it should "run the simulation until a condition is met" in:
    SimulationBinderImpl bind (
      SimulationEngine,
      terminalView
    ) withInitialState initialState runUntil (s => s.time.day.value < 5)

  it should "schedule the simulation with a scheduler" in:
    given ExecutionContext = ExecutionContext.global

    SimulationBinderImpl bind (
      SimulationEngine,
      terminalView
    ) withInitialState initialState runUntil (s =>
      s.time.day.value < 20
    ) scheduleWith CustomScheduler(100) run (runnable => runnable.run(), false)
