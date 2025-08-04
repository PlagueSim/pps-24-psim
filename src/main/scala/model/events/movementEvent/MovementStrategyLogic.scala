package model.events.movementEvent

import model.world.MovementComputation.PeopleMovement
import model.world.{Edge, LocalPercentageMovement, MovementStrategy, Node, Static, World}
import model.world.Types.*
object MovementStrategyLogic:
/*
* MovementStrategyLogic is responsible for computing the movement of people in the world based on their movement strategy.
* It uses the MovementStrategyDispatcher to determine the appropriate logic for the given strategy.
* It returns an iterable of PeopleMovement which contains the updated positions of nodes after applying the movement logic.
* */
  def compute(
               world: World,
               strategy: MovementStrategy,
               percentage: Percentage,
               rng: scala.util.Random
             ): Iterable[PeopleMovement] =
    
    MovementStrategyDispatcher.logicFor(strategy)
      .compute(world, percentage, rng)
