import controller.ExecutionMode.GuiFXMode
import dsl.DSL.*
import dsl.builders.SetupBuilderAndRunner
import model.core.SimulationState
import model.cure.CureModifiers
import model.infection.{DeathTypes, InfectionTypes}
import model.reaction.Reactions
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

    val preSelectionNodes = WorldFactory.createInitialWorld().nodes
    val postSelectionNodes = showStartPopup(preSelectionNodes)

    setup:
      simulationState:
        world:
          worldNodes:
            postSelectionNodes
          worldEdges:
            WorldFactory.createInitialWorld().edges
          worldMovements:
            WorldFactory.createInitialWorld().movements
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
            0.00
          cureModifiers:
            CureModifiers.empty
        time:
          initialState.time
        infectionLogic:
          InfectionTypes.SIRLogic
        deathLogic:
          DeathTypes.ProbabilisticDeath
        reactions:
          Reactions.StandardReactions
      conditions: (s: SimulationState) =>
        s.cure.progress < 1.0 && s.world.nodes.map(_._2.population).sum > 0
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
