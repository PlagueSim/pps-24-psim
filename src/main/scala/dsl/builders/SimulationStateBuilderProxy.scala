package dsl.builders

import model.world.World
import model.plague.Disease
import model.cure.Cure
import model.time.Time
import model.infection.PopulationStrategy
import model.core.SimulationState

class SimulationStateBuilderProxy(get: () => SimulationStateBuilder,
                                  set: SimulationStateBuilder => Unit) extends SimulationStateBuilder:
  override def withWorld(world: World): SimulationStateBuilder =
    val updated = get().withWorld(world)
    set(updated)
    updated

  override def withDisease(disease: Disease): SimulationStateBuilder =
    val updated = get().withDisease(disease)
    set(updated)
    updated

  override def withCure(cure: Cure): SimulationStateBuilder =
    val updated = get().withCure(cure)
    set(updated)
    updated

  override def withTime(time: Time): SimulationStateBuilder =
    val updated = get().withTime(time)
    set(updated)
    updated

  override def withInfectionLogic(infectionLogic: PopulationStrategy): SimulationStateBuilder =
    val updated = get().withInfectionLogic(infectionLogic)
    set(updated)
    updated
  
  override def build(): SimulationState = 
    get().build()
