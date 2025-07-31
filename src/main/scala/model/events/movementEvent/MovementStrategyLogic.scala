package model.events.movementEvent

import model.world.MovementComputation.PeopleMovement
import model.world.{Edge, GlobalLogicStrategy, GlobalRandomMovement, LocalPercentageMovement, LocalPercentageMovementStrategy, MovementStrategy, Node, Static, World}
object MovementStrategyLogic:

  def compute(
               world: World,
               strategy: MovementStrategy,
               percentage: Double,
               rng: scala.util.Random
             ): Iterable[PeopleMovement] =
    
    MovementStrategyDispatcher.logicFor(strategy)
      .compute(world, percentage, rng)
