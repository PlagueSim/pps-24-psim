import controller.ExecutionMode.TerminalMode
import dsl.DSL.*
import model.cure.CureModifiers
import model.infection.{DeathTypes, InfectionTypes}
import model.reaction.Reactions
import model.scheduler.CustomScheduler
import model.time.BasicYear
import model.time.TimeTypes.{Day, Year}
import model.world.WorldFactory
import utils.Utils.*
import view.ConsoleSimulationView

@main def runConsole(): Unit =

  val initialWorld = WorldFactory.createWorldWithInfected()

  setup:
    simulationState:
      world:
        worldNodes:
          initialWorld.nodes
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
      winCondition:
        WIN_CONDITION
      loseCondition:
        LOSE_CONDITION
      scheduler:
        CustomScheduler(SCHEDULING_STEP)
    binding:
      ConsoleSimulationView()
    runMode:
      TerminalMode

