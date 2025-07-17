package dsl.builders

import model.cure.Cure
import model.infection.InfectionAndDeathPopulation.PopulationStrategy
import model.plague.Disease
import model.time.Time
import model.world.World
import model.core.SimulationState

case class SimulationStateBuilder(time: Time = null,
                             disease: Disease = null,
                             cure: Cure = null,
                             world: World = null,
                             infectionLogic: PopulationStrategy = null):
  def withWorld(world: World): SimulationStateBuilder = 
    copy(world = world)
  
  def withDisease(disease: Disease): SimulationStateBuilder =
    copy(disease = disease)
    
  def withCure(cure: Cure): SimulationStateBuilder =
    copy(cure = cure)
    
  def withTime(time: Time): SimulationStateBuilder =
    copy(time = time)
  
  def withInfectionLogic(infectionLogic: PopulationStrategy): SimulationStateBuilder =
    copy(infectionLogic = infectionLogic)
  
  def build(): SimulationState =
    SimulationState(time, disease, cure, world, infectionLogic)
