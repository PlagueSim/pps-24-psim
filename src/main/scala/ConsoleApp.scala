import controller.ExecutionMode.TerminalMode
import dsl.DSL.*
import model.core.SimulationState
import model.cure.CureModifiers
import model.scheduler.CustomScheduler
import view.ConsoleSimulationView

@main def runConsole(): Unit =
  val initialState = SimulationState.createStandardSimulationState()

  setup:
    simulationState:
      world:
        world:
          worldNodes:
            initialState.world.nodes
          worldEdges:
            initialState.world.edges
          worldMovements:
            initialState.world.movements
      disease:
        diseaseName:
          "Diesease X"
        diseaseTraits:
          Set.empty
        diseasePoints:
          10
      cure:
        cureProgress:
          0.0
        cureBaseSpeed:
          1.0
        cureModifiers:
          CureModifiers.empty
      time:
        initialState.time
      infectionLogic:
        initialState.infectionLogic
      deathLogic:
        initialState.deathLogic
      reactions:
        initialState.reactions
    conditions: (s: SimulationState) =>
      s.time.day.value < 50
    scheduler:
      CustomScheduler(500)
    bindings:
      ConsoleSimulationView()
    runMode:
      TerminalMode
