package dsl

import controller.ExecutionMode.ExecutionMode
import dsl.builders.SetupBuilderAndRunner
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

  def setup(init: SetupBuilderAndRunner ?=> Unit): Unit =
    given builder: SetupBuilderAndRunner = SetupBuilderAndRunner()
    init
    builder.run()

  def simulationState(init: SimulationStateBuilder ?=> Unit)(using
      sb: SetupBuilderAndRunner
  ): Unit =
    var current: SimulationStateBuilder        = SimulationStateBuilder()
    given stateBuilder: SimulationStateBuilder =
      SimulationStateBuilderProxy(() => current, updated => current = updated)
    init
    sb.addSimulationState(stateBuilder.build())

  def conditions(init: SetupBuilderAndRunner ?=> SimulationState => Boolean)(using
                                                                             sb: SetupBuilderAndRunner
  ): Unit =
    sb.addConditions(init)

  def scheduler(init: SetupBuilderAndRunner ?=> Scheduler)(using
                                                           sb: SetupBuilderAndRunner
  ): Unit =
    sb.addScheduler(init)

  def bindings(init: SetupBuilderAndRunner ?=> UpdatableView)(using
                                                              sb: SetupBuilderAndRunner
  ): Unit =
    sb.setView(init)

  def runMode(init: SetupBuilderAndRunner ?=> ExecutionMode)(using
                                                             sb: SetupBuilderAndRunner
  ): Unit =
    sb.addRun(init)
