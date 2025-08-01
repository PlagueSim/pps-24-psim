package model.events.movementEvent
import model.world.MovementComputation.PeopleMovement
import model.world.{Edge, Node, World}

trait MovementLogic:
  /*
   * Computes the movement of people based on the given world state, 
   * percentage, and random number generator.
   */
  def compute(
               world: World,
               percent: Double,
               rng: scala.util.Random
             ): Iterable[PeopleMovement]
