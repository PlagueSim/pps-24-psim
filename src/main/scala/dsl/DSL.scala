package dsl

import controller.ExecutionMode.ExecutionMode
import dsl.builders.SetupBuilder
import dsl.builders.SimulationState.{
  SimulationStateBuilder,
  SimulationStateBuilderProxy
}
import model.core.SimulationState
import model.scheduler.Scheduler
import view.updatables.UpdatableView

object DSL:

  export dsl.builders.disease.DiseaseDSL.*
  export dsl.builders.cure.CureDSL.*
  export dsl.builders.SimulationState.SimStateDSL.*
  export dsl.builders.world.WorldDSL.{
    world,
    worldNodes,
    worldEdges,
    worldMovements
  }

  def setup(init: SetupBuilder ?=> Unit): Unit =
    given builder: SetupBuilder = SetupBuilder()
    init
    builder.build()

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

  def scheduler(init: SetupBuilder ?=> Scheduler)(using
      sb: SetupBuilder
  ): Unit =
    sb.addScheduler(init)

  def bindings(init: SetupBuilder ?=> UpdatableView)(using
      sb: SetupBuilder
  ): Unit =
    sb.setView(init)

  def runMode(init: SetupBuilder ?=> ExecutionMode)(using
      sb: SetupBuilder
  ): Unit =
    sb.addRun(init)
