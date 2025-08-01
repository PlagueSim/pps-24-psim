package model.world

import model.events.movementEvent.MovementStrategyLogic

object MovementComputation:

  case class PeopleMovement(from: String, to: String, amount: Int)
  case class MovementResult(updatedNodes: Map[String, Node], moves: List[PeopleMovement])

  /*
  * MovementComputation is responsible for computing the movement of people in the world.
  * It iterates over all movements defined in the world and applies the corresponding movement strategies.
  * It returns a MovementResult containing the updated nodes and a list of all movements made.
  * The computation is done in a functional style, where each movement strategy is applied to the current state of the world,
  * and the resulting movements are accumulated.
  * */
  
  def computeAllMovements(world: World, rng: scala.util.Random): MovementResult =
    world.movements.foldLeft(MovementResult(world.nodes, List.empty)) {
      case (MovementResult(currentNodes, accMoves), (strategy, percent)) =>

        val newMoves = MovementStrategyLogic
          .compute(world, strategy, percent, rng)

        val updatedNodes = World
          .applyMovements(world.modifyNodes(currentNodes), newMoves)
          .nodes

        MovementResult(updatedNodes, accMoves ++ newMoves)
    }
