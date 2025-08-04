import controller.ExecutionMode.GuiFXMode
import dsl.DSL.*
import dsl.builders.SetupBuilderAndRunner
import model.cure.CureModifiers
import model.infection.{DeathTypes, InfectionTypes}
import model.reaction.Reactions
import model.scheduler.CustomScheduler
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}
import model.world.WorldFactory
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.stage.Screen
import utils.Utils.*
import view.MainView
import view.intro.showStartPopup

object App extends JFXApp3:
  override def start(): Unit =
    val X = Screen.primary.bounds.width
    val Y = Screen.primary.bounds.height
    val mainView = MainView()
    val initialWorld = WorldFactory.createInitialWorld()
    val preSelectionNodes = initialWorld.nodes

    val postSelectionNodes = showStartPopup(preSelectionNodes)

    setup:
      simulationState:
        world:
          worldNodes:
            postSelectionNodes
          worldEdges:
            initialWorld.edges
          worldMovements:
            initialWorld.movements
        disease:
          diseaseName:
            "Diesease X"
          diseaseTraits:
            Set.empty
          diseasePoints:
            DISEASE_POINTS
        cure:
          cureProgress:
            CURE_PROGRESS
          cureBaseSpeed:
            CURE_BASE_SPEED
          cureModifiers:
            CureModifiers.empty
        time:
          BasicYear(Day(DAY_ZERO), Year(YEAR_ZERO))
        infectionLogic:
          InfectionTypes.SIRLogic
        deathLogic:
          DeathTypes.ProbabilisticDeath
        reactions:
          Reactions.StandardReactions
      conditions: 
        STANDARD_CONDITION
      scheduler:
        CustomScheduler(SCHEDULING_STEP)
      binding:
        mainView
      runMode:
        GuiFXMode

    stage = new JFXApp3.PrimaryStage:
      title = "Plague Sim"
      scene = new Scene:
        root = mainView
      width = X * MAX_WIDTH
      height = Y * MAX_HEIGHT
      minWidth = X * MIN_WIDTH
      minHeight = Y * MIN_HEIGHT
