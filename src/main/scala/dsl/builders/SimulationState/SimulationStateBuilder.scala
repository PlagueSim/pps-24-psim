package dsl.builders.SimulationState

import model.core.SimulationState
import model.cure.Cure
import model.infection.PopulationEffect
import model.plague.Disease
import model.time.Time
import model.world.World

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
      SimulationState.createStandardSimulationState().reactions,
):
  def withWorld(world: World): SimulationStateBuilder =
    copy(world = world)

  def withDisease(disease: Disease): SimulationStateBuilder =
    copy(disease = disease)

  def withCure(cure: Cure): SimulationStateBuilder =
    copy(cure = cure)

  def withTime(time: Time): SimulationStateBuilder =
    copy(time = time)

  def withInfectionLogic(
      infectionLogic: PopulationEffect
  ): SimulationStateBuilder =
    copy(infectionLogic = infectionLogic)

  def withDeathLogic(deathLogic: PopulationEffect): SimulationStateBuilder =
    copy(deathLogic = deathLogic)

  def withReactions(
      reactions: model.reaction.Reactions
  ): SimulationStateBuilder =
    copy(reactions = reactions)

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
