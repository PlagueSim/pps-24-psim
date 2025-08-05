package dsl.builders.SimulationState

import model.infection.PopulationEffect
import model.reaction.Reactions
import model.time.Time
import model.world.World

/** Provides a DSL for configuring the components of a SimulationState.
  */
object SimStateDSL:

  /** Sets the initial time for the simulation state.
    */
  def time(init: SimulationStateBuilder ?=> Time)(using
      ssb: SimulationStateBuilder
  ): Unit =
    ssb.withTime(init)

  /** Sets the infection logic for the simulation state.
    */
  def infectionLogic(init: SimulationStateBuilder ?=> PopulationEffect)(using
      ssb: SimulationStateBuilder
  ): Unit =
    ssb.withInfectionLogic(init)

  /** Sets the death logic for the simulation state.
    * @param init
    *   A block of code that returns a `PopulationEffect` for death.
    * @param ssb
    *   The `SimulationStateBuilder` to which the death logic will be added.
    */
  def deathLogic(init: SimulationStateBuilder ?=> PopulationEffect)(using
      ssb: SimulationStateBuilder
  ): Unit =
    ssb.withDeathLogic(init)

  /** Sets the reactions for the simulation state.
    * @param init
    *   A block of code that returns a `Reactions` instance.
    * @param ssb
    *   The `SimulationStateBuilder` to which the reactions will be added.
    */
  def reactions(init: SimulationStateBuilder ?=> Reactions)(using
      ssb: SimulationStateBuilder
  ): Unit =
    ssb.withReactions(init)
