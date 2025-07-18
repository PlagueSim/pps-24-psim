import controller.ExecutionMode.GuiFXMode
import dsl.DSL.*
import dsl.builders.{SetupBuilder, SimulationStateBuilder}
import model.core.SimulationState
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.stage.Screen
import view.MainView

object App extends JFXApp3:
  override def start(): Unit =
    val X = Screen.primary.bounds.width
    val Y = Screen.primary.bounds.height

    val mainView = MainView()

    val initialState: SimulationState =
      SimulationState.createStandardSimulationState()

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
