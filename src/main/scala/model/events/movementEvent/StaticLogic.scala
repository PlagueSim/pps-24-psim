package model.events.movementEvent

import model.world.MovementComputation.PeopleMovement
import model.world.{Node, World}
import model.world.Types.*
/*
* StaticLogic is a MovementLogic implementation that does not perform any movements.
* It returns an empty list of PeopleMovement, indicating no changes in the world state.
*/
object StaticLogic extends MovementLogic:
  override def compute(
                        world: World,
                        percent: Percentage,
                        rng: scala.util.Random
                      ): List[PeopleMovement] = List.empty

