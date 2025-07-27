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
             ): List[(String, String)] = strategy match
    case Static => Nil
    case s: GlobalLogicStrategy =>
      val count = param.toInt
      GlobalRandomLogic.compute(nodes, count, neighbors, isEdgeOpen, rng)
    case s: LocalPercentageMovementStrategy =>
      val percent = param
      LocalPercentageLogic.compute(nodes, percent, neighbors, isEdgeOpen, rng)
