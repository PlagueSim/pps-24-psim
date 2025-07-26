package model.events.movementEvent

import model.world.{MovementStrategy, Node, RandomNeighbor, RandomWorld, Static}

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
    case RandomWorld =>
      val peopleToMove = param.toInt
      RandomWorldLogic.compute(nodes, peopleToMove, neighbors, isEdgeOpen, rng)
    case RandomNeighbor =>
      val percent = param
      RandomNeighborLogic.compute(nodes, percent, neighbors, isEdgeOpen, rng)
