package model.events.movementEvent

import model.world.{MovementStrategy, Node, LocalPercentageMovement, GlobalRandomMovement, Static}

object MovementStrategyLogic:

  def compute(
               strategy: MovementStrategy,
               nodes: Map[String, Node],
               param: Double, // può essere numero assoluto o percentuale
               neighbors: String => Set[String],
               isEdgeOpen: (String, String) => Boolean,
               rng: scala.util.Random
             ): List[(String, String)] = strategy match
    case Static => Nil
    case GlobalRandomMovement =>
      val peopleToMove = param.toInt
      RandomWorldLogic.compute(nodes, peopleToMove, neighbors, isEdgeOpen, rng)
    case LocalPercentageMovement =>
      val percent = param
      RandomNeighborLogic.compute(nodes, percent, neighbors, isEdgeOpen, rng)
