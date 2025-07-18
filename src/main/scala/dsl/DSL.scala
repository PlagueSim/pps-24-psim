package dsl

import controller.ExecutionMode.ExecutionMode
import dsl.builders.{
  SetupBuilder,
  SimulationStateBuilder,
  SimulationStateBuilderProxy
}
import model.core.SimulationState
import model.cure.Cure
import model.infection.InfectionAndDeathPopulation.PopulationStrategy
import model.plague.Disease
import model.time.Time
import model.world.World
import view.updatables.UpdatableView

object DSL:

  // trait PsimApplication:

  def setup(init: SetupBuilder ?=> Unit): Unit =
    given bulder: SetupBuilder = SetupBuilder()
    init
    bulder.build()

  def simulationState(init: SimulationStateBuilder ?=> Unit)(using
      sb: SetupBuilder
  ): Unit =
    var current: SimulationStateBuilder        = SimulationStateBuilder()
    given stateBuilder: SimulationStateBuilder =
      SimulationStateBuilderProxy(() => current, updated => current = updated)
    init
    sb.addSimulationState(stateBuilder.build())

  def conditions(init: SetupBuilder ?=> SimulationState => Boolean)(using
      sb: SetupBuilder
  ): Unit =
    sb.addConditions(init)

  def bindings(init: SetupBuilder ?=> UpdatableView)(using
      sb: SetupBuilder
  ): Unit =
    sb.setView(init)

  def runMode(init: SetupBuilder ?=> ExecutionMode)(using
      sb: SetupBuilder
  ): Unit =
    sb.addRun(init)

  def world(init: SimulationStateBuilder ?=> World)(using
      ssb: SimulationStateBuilder
  ): Unit =
    ssb.withWorld(init)

  def cure(init: SimulationStateBuilder ?=> Cure)(using
      ssb: SimulationStateBuilder
  ): Unit =
    ssb.withCure(init)

  def disease(init: SimulationStateBuilder ?=> Disease)(using
      ssb: SimulationStateBuilder
  ): Unit =
    ssb.withDisease(init)

  def time(init: SimulationStateBuilder ?=> Time)(using
      ssb: SimulationStateBuilder
  ): Unit =
    ssb.withTime(init)

  def infectionLogic(init: SimulationStateBuilder ?=> PopulationStrategy)(using
      ssb: SimulationStateBuilder
  ): Unit =
    ssb.withInfectionLogic(init)
