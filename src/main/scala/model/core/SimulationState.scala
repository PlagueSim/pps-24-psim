package model.core

import model.cure.Cure
import model.infection.InfectionAndDeathPopulation.*
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
  /** Creates a new SimulationState with the provided parameters.
   *
   * @param time
   *   The initial time of the simulation.
   * @param disease
   *   The initial disease in the simulation.
   * @param cure
   *   The initial cure in the simulation.
   * @param world
   *   The initial world in the simulation.
   *   @param infectionLogic
   *   The logic for infection in the simulation.
   *   @param deathLogic
   *   The logic for death in the simulation.
   *   @param reactions
   *   The reactions in the simulation.
   * @return
   *   A new SimulationState instance.
   */
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
    val STARTING_DAY: Int  = 0
    val STARTING_YEAR: Int = 2025

    val node = Node.withPopulation(100).withInfected(1).build()

    val world = World(
      Map("A" -> node, "B" -> node),
      Map("A-B" -> Edge("A", "B", EdgeType.Land)),
      Map(Static -> 0.3, GlobalRandomMovement -> 0.4, LocalPercentageMovement -> 0.3)
    )

    SimulationState(
      BasicYear(Day(STARTING_DAY), Year(STARTING_YEAR)),
      Disease("StandardDisease", Set(pulmonaryEdema), 1),
      Cure(),
      world,
      StandardInfection,
      StandardDeath,
      StandardReactions
    )

  extension (state: SimulationState)
    /** Replaces the current state with a new value based on its type.
     *
     * @param newValue
     *   The new value to replace in the state.
     * @return
     *   A new SimulationState with the updated value.
     */
    def replace[A](newValue: A): SimulationState = newValue match
      case newTime: Time                    => state.copy(time = newTime)
      case newDisease: Disease              => state.copy(disease = newDisease)
      case newCure: Cure                    => state.copy(cure = newCure)
      case newWorld: World                  => state.copy(world = newWorld)
      case newInfection: PopulationStrategy=>
        state.copy(infectionLogic = newInfection)
      case newReactions: Reactions =>
        state.copy(reactions = newReactions)
      case _ => state
