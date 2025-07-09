import controller.SimulationBinderImpl
import model.core.SimulationEngine
import model.scheduler.*
import scalafx.application.{JFXApp3, Platform}
import scalafx.scene.Scene
import scalafx.stage.Screen
import cats.effect.unsafe.implicits.global
import view.MainView

object App extends JFXApp3:
  override def start(): Unit =
    val X = Screen.primary.bounds.width
    val Y = Screen.primary.bounds.height

    val mainView = MainView()

    val initialState = SimulationEngine.initialState

    given execContext: scala.concurrent.ExecutionContext =
      scala.concurrent.ExecutionContext.global

    SimulationBinderImpl bind (
      SimulationEngine,
      mainView
    ) withInitialState initialState runUntil (s =>
      s.time.day.value < 20
    ) scheduleWith CustomScheduler(500) run (Platform.runLater, false)


    stage = new JFXApp3.PrimaryStage:
      title = "Plague Sim"
      scene = new Scene:
        root = mainView
      width = X * 0.66
      height = Y * 0.66
      minWidth = X * 0.33
      minHeight = Y * 0.33
