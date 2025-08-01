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

/** Provides a Domain-Specific Language (DSL) for creating and configuring
  * simulations.
  */
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

  /** Sets up and runs the simulation. This is the main entry point of the DSL.
    */
  def setup(init: SetupBuilderAndRunner ?=> Unit): Unit =
    given builder: SetupBuilderAndRunner = SetupBuilderAndRunner()
    init
    builder.buildAndRun()

  /** Defines a single state of the simulation.
    */
  def simulationState(init: SimulationStateBuilder ?=> Unit)(using
      sb: SetupBuilderAndRunner
  ): Unit =
    var current: SimulationStateBuilder        = SimulationStateBuilder()
    given stateBuilder: SimulationStateBuilder =
      SimulationStateBuilderProxy(() => current, updated => current = updated)
    init
    sb.addSimulationState(stateBuilder.build())

  /** Defines a condition that must be met for the simulation to continue.
    */
  def conditions(init: SetupBuilderAndRunner ?=> SimulationState => Boolean)(
      using sb: SetupBuilderAndRunner
  ): Unit =
    sb.addConditions(init)

  /** Defines a scheduler for the simulation, which controls the timing of
    * events.
    */
  def scheduler(init: SetupBuilderAndRunner ?=> Scheduler)(using
      sb: SetupBuilderAndRunner
  ): Unit =
    sb.addScheduler(init)

  /** Binds the view to the simulation.
    */
  def binding(init: SetupBuilderAndRunner ?=> UpdatableView)(using
      sb: SetupBuilderAndRunner
  ): Unit =
    sb.setView(init)

  /** Defines the execution mode of the simulation, which determines how the
    * simulation runs.
    */
  def runMode(init: SetupBuilderAndRunner ?=> ExecutionMode)(using
      sb: SetupBuilderAndRunner
  ): Unit =
    sb.addRun(init)
