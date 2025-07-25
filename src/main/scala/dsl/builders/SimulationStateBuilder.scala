package dsl.builders

import model.cure.Cure
import model.infection.PopulationStrategy
import model.plague.Disease
import model.time.Time
import model.world.World
import model.core.SimulationState
import model.scheduler.Scheduler

case class SimulationStateBuilder(
    time: Time = SimulationState.createStandardSimulationState().time,
    disease: Disease = SimulationState.createStandardSimulationState().disease,
    cure: Cure = SimulationState.createStandardSimulationState().cure,
    world: World = SimulationState.createStandardSimulationState().world,
    infectionLogic: PopulationStrategy =
      SimulationState.createStandardSimulationState().infectionLogic,
    deathLogic: PopulationStrategy =
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
      infectionLogic: PopulationStrategy
  ): SimulationStateBuilder =
    copy(infectionLogic = infectionLogic)

  def withDeathLogic(deathLogic: PopulationStrategy): SimulationStateBuilder =
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
