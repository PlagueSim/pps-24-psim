package model.world

import model.events.movementEvent.MovementStrategyLogic
import Types.*
object MovementComputation:

  case class PeopleMovement(from: NodeId, to: NodeId, amount: Int)
  case class MovementResult(updatedNodes: Map[NodeId, Node], moves: List[PeopleMovement])

  /**
  * MovementComputation is responsible for computing the movement of people in the world.
  * It iterates over all movements defined in the world and applies the corresponding movement strategies.
  * It returns a MovementResult containing the updated nodes and a list of all movements made.
  * The computation is done in a functional style, where each movement strategy is applied to the current state of the world,
  * and the resulting movements are accumulated.
  *
  * @param world The current state of the world containing nodes, edges, and movement strategies.
  * @param rng A random number generator used for probabilistic movement strategies.
  * 
   * @return A MovementResult containing the updated nodes and a list of all movements made.
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
