package dsl.builders.SimulationState

import model.core.SimulationState
import model.cure.Cure
import model.infection.PopulationEffect
import model.plague.Disease
import model.reaction.Reactions
import model.time.Time
import model.world.World

/** A proxy for [[SimulationStateBuilder]] that allows modifying a simulation
  * state builder instance through a getter and a setter.
  */
class SimulationStateBuilderProxy(get: () => SimulationStateBuilder,
                                  set: SimulationStateBuilder => Unit) extends SimulationStateBuilder:
  /** Sets the world for the simulation and updates the underlying builder
    * instance.
    */
  override def withWorld(world: World): SimulationStateBuilder =
    val updated = get().withWorld(world)
    set(updated)
    updated

  /** Sets the disease for the simulation and updates the underlying builder
    * instance.
    */
  override def withDisease(disease: Disease): SimulationStateBuilder =
    val updated = get().withDisease(disease)
    set(updated)
    updated

  /** Sets the cure for the simulation and updates the underlying builder
    * instance.
    */
  override def withCure(cure: Cure): SimulationStateBuilder =
    val updated = get().withCure(cure)
    set(updated)
    updated

  /** Sets the time for the simulation and updates the underlying builder
    * instance.
    */
  override def withTime(time: Time): SimulationStateBuilder =
    val updated = get().withTime(time)
    set(updated)
    updated

  /** Sets the infection logic for the simulation and updates the underlying
    * builder instance.
    */
  override def withInfectionLogic(infectionLogic: PopulationEffect): SimulationStateBuilder =
    val updated = get().withInfectionLogic(infectionLogic)
    set(updated)
    updated
    
  /** Sets the death logic for the simulation and updates the underlying builder
    * instance.
    */
  override def withDeathLogic(deathLogic: PopulationEffect): SimulationStateBuilder =
    val updated = get().withDeathLogic(deathLogic)
    set(updated)
    updated
    
  /** Sets the reactions for the simulation and updates the underlying builder
    * instance.
    */
  override def withReactions(reactions: Reactions): SimulationStateBuilder =
    val updated = get().withReactions(reactions)
    set(updated)
    updated

  /** Builds and returns a [[SimulationState]] instance using the underlying
    * builder.
    */
  override def build(): SimulationState = 
    get().build()
