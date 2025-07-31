package model.events.movementEvent

import model.world.{Edge, GlobalRandomMovement, LocalPercentageMovement, MovementStrategy, Node, Static, World}
import model.core.SimulationState
import model.events.Event
import model.world.MovementComputation
case class MovementEvent() extends Event[Map[String, Node]]:

  override def modifyFunction(s: SimulationState): Map[String, Node] =
    val rng = new scala.util.Random()
    MovementComputation.computeAllMovements(s.world, rng).updatedNodes
