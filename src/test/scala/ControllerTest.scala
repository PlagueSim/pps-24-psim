import controller.ExecutionMode.TerminalMode
import controller.SimulationBinderImpl
import model.core.{SimulationEngine, SimulationState}
import model.scheduler.CustomScheduler
import model.time.TimeTypes.*
import model.world.{MovementStrategy, Static}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import view.updatables.UpdatableView

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
  val initialState: SimulationState = SimulationState.createStandardSimulationState()

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
    SimulationBinderImpl bind (
      SimulationEngine,
      terminalView
    ) withInitialState initialState runUntil (s =>
      s.time.day.value < 20
    ) scheduleWith CustomScheduler(100) run TerminalMode

  it should "print the correct day" in :
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream) {
      SimulationBinderImpl bind (
        SimulationEngine,
        terminalView
      ) withInitialState initialState runUntil (s =>
        s.time.day.value < 20
        ) scheduleWith CustomScheduler(100) run TerminalMode
    }
    stream.toString should include("Current Time: 20 of 2025")
