package dsl.builders.SimulationState

import model.core.SimulationState
import model.cure.Cure
import model.infection.PopulationEffect
import model.plague.Disease
import model.time.Time
import model.world.World

/** A builder for creating instances of [[SimulationState]].
  */
case class SimulationStateBuilder(
    time: Time = SimulationState.createStandardSimulationState().time,
    disease: Disease = SimulationState.createStandardSimulationState().disease,
    cure: Cure = SimulationState.createStandardSimulationState().cure,
    world: World = SimulationState.createStandardSimulationState().world,
    infectionLogic: PopulationEffect =
      SimulationState.createStandardSimulationState().infectionLogic,
    deathLogic: PopulationEffect =
      SimulationState.createStandardSimulationState().deathLogic,
    reactions: model.reaction.Reactions =
      SimulationState.createStandardSimulationState().reactions
):
  /** Sets the world for the simulation.
    */
  def withWorld(world: World): SimulationStateBuilder =
    copy(world = world)

  /** Sets the disease for the simulation.
    */
  def withDisease(disease: Disease): SimulationStateBuilder =
    copy(disease = disease)

  /** Sets the cure for the simulation.
    */
  def withCure(cure: Cure): SimulationStateBuilder =
    copy(cure = cure)

  /** Sets the time for the simulation.
    */
  def withTime(time: Time): SimulationStateBuilder =
    copy(time = time)

  /** Sets the infection logic for the simulation.
    */
  def withInfectionLogic(
      infectionLogic: PopulationEffect
  ): SimulationStateBuilder =
    copy(infectionLogic = infectionLogic)

  /** Sets the death logic for the simulation.
    */
  def withDeathLogic(deathLogic: PopulationEffect): SimulationStateBuilder =
    copy(deathLogic = deathLogic)

  /** Sets the reactions for the simulation.
    */
  def withReactions(
      reactions: model.reaction.Reactions
  ): SimulationStateBuilder =
    copy(reactions = reactions)

  /** Builds and returns a [[SimulationState]] instance with the configured
    * parameters.
    */
  def build(): SimulationState =
    SimulationState(
      time,
      disease,
      cure,
      world,
      infectionLogic,
      deathLogic,
      reactions
    )
