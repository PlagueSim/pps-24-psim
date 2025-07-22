package model.core

import model.cure.Cure
import model.infection.InfectionAndDeathPopulation.Infection.Death.StandardDeath
import model.infection.InfectionAndDeathPopulation.PopulationStrategy
import model.infection.InfectionAndDeathPopulation.Infection.StandardInfection
import model.plague.Disease
import model.plague.Symptoms.pulmonaryEdema
import model.reaction.Reactions
import model.reaction.Reactions.StandardReactions
import model.time.TimeTypes.{Day, Year}
import model.time.{BasicYear, Time}
import model.world.*

sealed case class SimulationState private (
                                            time: Time,
                                            disease: Disease,
                                            cure: Cure,
                                            world: World,
                                            infectionLogic: PopulationStrategy,
                                            deathLogic: PopulationStrategy,
                                            reactions: Reactions
                                          )

object SimulationState:
  def apply(
             time: Time,
             disease: Disease,
             cure: Cure,
             world: World,
             infectionLogic: PopulationStrategy,
             deathLogic: PopulationStrategy,
             reactions: Reactions
           ): SimulationState =
    new SimulationState(time, disease, cure, world, infectionLogic, deathLogic, reactions)

  def createStandardSimulationState(): SimulationState =
    val STARTING_DAY = 0
    val STARTING_YEAR = 2025

    val baseWorld = WorldFactory.mockWorld()
    val edgeId = "A-H-Sea"

    val closedWorld = baseWorld.modifyEdges(
      baseWorld.edges.updated(edgeId, baseWorld.edges(edgeId).close)
    )


    SimulationState(
      BasicYear(Day(STARTING_DAY), Year(STARTING_YEAR)),
      Disease("StandardDisease", Set(pulmonaryEdema), 1),
      Cure(),
      baseWorld,
      StandardInfection,
      StandardDeath,
      StandardReactions
    )

  extension (state: SimulationState)
    def replace[A](newValue: A): SimulationState = newValue match
      case newTime: Time                    => state.copy(time = newTime)
      case newDisease: Disease              => state.copy(disease = newDisease)
      case newCure: Cure                    => state.copy(cure = newCure)
      case newWorld: World                  => state.copy(world = newWorld)
      case newInfection: PopulationStrategy => state.copy(infectionLogic = newInfection)
      case newReactions: Reactions          => state.copy(reactions = newReactions)
      case _                                => state
