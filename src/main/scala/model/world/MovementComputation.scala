package model.world

import model.events.movementEvent.MovementStrategyLogic

object MovementComputation:

  case class PeopleMovement(from: String, to: String, amount: Int)
  case class MovementResult(updatedNodes: Map[String, Node], moves: List[PeopleMovement])

  def computeAllMovements(world: World, rng: scala.util.Random): MovementResult =
    world.movements.foldLeft(MovementResult(world.nodes, List.empty)) {
      case (MovementResult(currentNodes, accMoves), (strategy, percent)) =>
        
        val newMoves = MovementStrategyLogic
          .compute(world, strategy, percent, rng)

        val updatedNodes = World
          .applyMovements(world.modifyNodes(currentNodes), newMoves.map(m => (m.from, m.to, m.amount)))
          .nodes

        MovementResult(updatedNodes, accMoves ++ newMoves)
    }
