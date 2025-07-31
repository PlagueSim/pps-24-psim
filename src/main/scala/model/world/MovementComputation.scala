package model.world

import model.events.movementEvent.MovementStrategyLogic

object MovementComputation:

  case class MovementResult(updatedNodes: Map[String, Node], moves: List[(String, String, Int)])

  def computeAllMovements(world: World, rng: scala.util.Random): MovementResult =
    world.movements.foldLeft(MovementResult(world.nodes, List.empty)) {
      case (MovementResult(currentNodes, accMoves), (strategy, percent)) =>
        val newMoves = MovementStrategyLogic.compute(world, strategy, percent, rng)
        val updatedNodes = World.applyMovements(world.modifyNodes(currentNodes), newMoves).nodes
        MovementResult(updatedNodes, accMoves ++ newMoves)
    }

