package model.events.movementEvent

import model.world.{MovementStrategy, GlobalLogicStrategy,LocalPercentageMovementStrategy, Node, LocalPercentageMovement, GlobalRandomMovement, Static}
object MovementStrategyLogic:

  def compute(
               strategy: MovementStrategy,
               nodes: Map[String, Node],
               param: Double,
               neighbors: String => Set[String],
               isEdgeOpen: (String, String) => Boolean,
               rng: scala.util.Random
             ): List[(String, String, Int)] =
    
    MovementStrategyDispatcher.logicFor(strategy)
      .compute(nodes, param, neighbors, isEdgeOpen, rng)
