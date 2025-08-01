package model.events.movementEvent

import model.world.{Edge, LocalPercentageMovement, MovementStrategy, Node, Static, World}
import model.core.SimulationState
import model.events.Event
import model.world.MovementComputation
case class MovementEvent() extends Event[Map[String, Node]]:
  /*
  * MovementEvent is responsible for computing the movement of people in the world.
  * It uses the MovementComputation to determine how nodes should move based on their movement strategy.
  * It generates a new state of the world with updated nodes after applying the movements.
  * */
  override def modifyFunction(s: SimulationState): Map[String, Node] =
    val rng = new scala.util.Random()
    MovementComputation.computeAllMovements(s.world, rng).updatedNodes
