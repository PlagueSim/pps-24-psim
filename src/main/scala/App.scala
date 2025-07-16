import controller.ExecutionMode.GuiFXMode
import controller.SimulationBinderImpl
import model.core.{SimulationEngine, SimulationState}
import model.scheduler.*
import model.time.TimeTypes.Day
import model.world.{MovementStrategy, Static}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.stage.Screen
import view.MainView

object App extends JFXApp3:
  override def start(): Unit =
    val X = Screen.primary.bounds.width
    val Y = Screen.primary.bounds.height

    val mainView = MainView()

    val movements: Map[MovementStrategy, Double] = Map(
      Static -> 1.0
    )

    val initialState: SimulationState = SimulationState.createStandardSimulationState()

    SimulationBinderImpl bind (
      SimulationEngine,
      mainView
    ) withInitialState initialState runUntil (s =>
      s.time.day.value < 20
    ) scheduleWith CustomScheduler(500) run GuiFXMode

    stage = new JFXApp3.PrimaryStage:
      title = "Plague Sim"
      scene = new Scene:
        root = mainView
      width = X * 0.66
      height = Y * 0.66
      minWidth = X * 0.33
      minHeight = Y * 0.33
