import controller.SimulationBinderImpl
import model.World.{MovementStrategy, Static, World}
import model.core.{SimulationEngine, SimulationState}
import model.cure.Cure
import model.plague.Disease
import model.scheduler.*
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}
import scalafx.application.{JFXApp3, Platform}
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

    val initialState: SimulationState = SimulationState(
      BasicYear(Day(0), Year(2023)),
      Disease("Pax-12", Set.empty, 1000),
      Cure(),
      World(Map.empty, Set.empty, movements)
    )

    given execContext: scala.concurrent.ExecutionContext =
      scala.concurrent.ExecutionContext.global

    SimulationBinderImpl bind (
      SimulationEngine,
      mainView
    ) withInitialState initialState runUntil (s =>
      s.time.day.value < 200
    ) scheduleWith CustomScheduler(500) run (Platform.runLater, true)

    stage = new JFXApp3.PrimaryStage:
      title = "Plague Sim"
      scene = new Scene:
        root = mainView
      width = X * 0.66
      height = Y * 0.66
      minWidth = X * 0.33
      minHeight = Y * 0.33
