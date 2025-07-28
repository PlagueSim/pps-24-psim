package dsl.builders.world

import dsl.builders.SimulationState.SimulationStateBuilder

object WorldDSL:
  def world(init: WorldBuilder ?=> Unit)
           (using ssb: SimulationStateBuilder): Unit = 
    var current: WorldBuilder = WorldBuilder()
    given worldBuilder: WorldBuilder = 
      WorldBuilderProxy(() => current, updated => current = updated)
    init
    ssb.withWorld(worldBuilder.build())
    
  def worldNodes(init: WorldBuilder ?=> Map[String, model.world.Node])
                (using wb: WorldBuilder): Unit =
    wb.withNodes(init)
    
  def worldEdges(init: WorldBuilder ?=> Map[String, model.world.Edge])
                  (using wb: WorldBuilder): Unit =
  wb.withEdges(init)
  
  def worldMovements(init: WorldBuilder ?=> Map[model.world.MovementStrategy, Double])
              (using wb: WorldBuilder): Unit =
  wb.withMovements(init)
  


