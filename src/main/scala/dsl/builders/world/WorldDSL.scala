package dsl.builders.world

import dsl.builders.SimulationState.SimulationStateBuilder
import model.world.{Edge, MovementStrategy, Node}

/**
 * Provides a DSL for configuring the world within a simulation state.
 */
object WorldDSL:
  /**
   * Defines the world for the current simulation state.
   */
  def world(init: WorldBuilder ?=> Unit)
           (using ssb: SimulationStateBuilder): Unit = 
    var current: WorldBuilder = WorldBuilder()
    given worldBuilder: WorldBuilder = 
      WorldBuilderProxy(() => current, updated => current = updated)
    init
    ssb.withWorld(worldBuilder.build())
    
  /**
   * Defines the nodes of the world.
   */
  def worldNodes(init: WorldBuilder ?=> Map[String, Node])
                (using wb: WorldBuilder): Unit =
    wb.withNodes(init)
    
  /**
   * Defines the edges of the world.
   */
  def worldEdges(init: WorldBuilder ?=> Map[String, Edge])
                  (using wb: WorldBuilder): Unit =
  wb.withEdges(init)
  
  /**
   * Defines the movement strategies for the world.
   */
  def worldMovements(init: WorldBuilder ?=> Map[MovementStrategy, Double])
              (using wb: WorldBuilder): Unit =
  wb.withMovements(init)
