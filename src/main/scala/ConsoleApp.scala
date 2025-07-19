import controller.ExecutionMode.TerminalMode
import dsl.DSL.*
import model.core.SimulationState
import view.ConsoleSimulationView

@main def runConsole(): Unit =
  val initialState = SimulationState.createStandardSimulationState()

  setup:
    simulationState:
      world:
        initialState.world
      disease:
        initialState.disease
      cure:
        initialState.cure
      time:
        initialState.time
      infectionLogic:
        initialState.infectionLogic
      deathLogic:
        initialState.deathLogic
    conditions: (s: SimulationState) =>
      s.time.day.value < 50
    bindings:
      ConsoleSimulationView()
    runMode:
      TerminalMode
