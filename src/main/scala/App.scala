import controller.ExecutionMode.GuiFXMode
import dsl.DSL.*
import dsl.builders.SetupBuilderAndRunner
import model.core.SimulationState
import model.cure.CureModifiers
import model.scheduler.CustomScheduler
import model.world.{World, WorldFactory}
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

    val initialState: SimulationState = SimulationState.createStandardSimulationState()

    val preSelectionNodes = WorldFactory.mockWorld().nodes
    val postSelectionNodes = showStartPopup(preSelectionNodes)

    setup:
      simulationState:
        world:
          worldNodes:
            postSelectionNodes
          worldEdges:
            WorldFactory.mockWorld().edges
          worldMovements:
            WorldFactory.mockWorld().movements
        disease:
          diseaseName:
            "Diesease X"
          diseaseTraits:
            Set.empty
          diseasePoints:
            100
        cure:
          cureProgress:
            0.0
          cureBaseSpeed:
            0.01
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
        s.time.day.value < 500
      scheduler:
        CustomScheduler(500)
      binding:
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
