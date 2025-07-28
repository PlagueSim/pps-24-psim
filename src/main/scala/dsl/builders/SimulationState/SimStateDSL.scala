package dsl.builders.SimulationState

import model.infection.PopulationEffect
import model.reaction.Reactions
import model.time.Time
import model.world.World

object SimStateDSL:
//  def world(init: SimulationStateBuilder ?=> World)(using
//                                                    ssb: SimulationStateBuilder
//  ): Unit =
//    ssb.withWorld(init)

  //  def cure(init: SimulationStateBuilder ?=> Cure)(using
  //      ssb: SimulationStateBuilder
  //  ): Unit =
  //    ssb.withCure(init)

  //  def disease(init: SimulationStateBuilder ?=> Disease)(using
  //      ssb: SimulationStateBuilder
  //  ): Unit =
  //    ssb.withDisease(init)

  def time(init: SimulationStateBuilder ?=> Time)(using
                                                  ssb: SimulationStateBuilder
  ): Unit =
    ssb.withTime(init)

  def infectionLogic(init: SimulationStateBuilder ?=> PopulationEffect)(using
                                                                        ssb: SimulationStateBuilder
  ): Unit =
    ssb.withInfectionLogic(init)

  def deathLogic(init: SimulationStateBuilder ?=> PopulationEffect)(using
                                                                    ssb: SimulationStateBuilder
  ): Unit =
    ssb.withDeathLogic(init)

  def reactions(init: SimulationStateBuilder ?=> Reactions)(using
                                                            ssb: SimulationStateBuilder
  ): Unit =
    ssb.withReactions(init)


