import controller.ExecutionMode.GuiFXMode
import dsl.DSL.*
import dsl.builders.SetupBuilder
import model.cure.CureModifiers
import model.core.SimulationState
import model.scheduler.CustomScheduler
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.stage.Screen
import view.MainView
import view.intro.showStartPopup

object App extends JFXApp3:
  override def start(): Unit =
    val X = Screen.primary.bounds.width
    val Y = Screen.primary.bounds.height

    val mainView = MainView()

    val preSelectionState =
      SimulationState.createStandardSimulationState()

    val initialState: SimulationState = showStartPopup(preSelectionState)

    setup:
      simulationState:
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
        mainView
      runMode:
        GuiFXMode

    stage = new JFXApp3.PrimaryStage:
      title = "Plague Sim"
      scene = new Scene:
        root = mainView
      width = X * 0.66
      height = Y * 0.66
      minWidth = X * 0.33
      minHeight = Y * 0.33
