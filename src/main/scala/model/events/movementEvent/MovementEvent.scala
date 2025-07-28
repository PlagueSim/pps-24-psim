package model.events.movementEvent

import model.world.{Edge, GlobalRandomMovement, LocalPercentageMovement, MovementStrategy, Node, Static, World}
import model.core.SimulationState
import model.events.Event

case class MovementEvent() extends Event[Map[String, Node]]:

  override def modifyFunction(s: SimulationState): Map[String, Node] =
    val rng = new scala.util.Random()
    val nodes = s.world.nodes

    computeAllMovements(rng, s.world)


  private def computeAllMovements(
                                   rng: scala.util.Random,
                                   world: World
                                 ): Map[String, Node] =
    world.movements.toList.foldLeft(world.nodes) {
      case (currentNodes, (strategy, percent)) =>
        val moves = MovementStrategyLogic.compute(world.modifyNodes(currentNodes), strategy, percent, rng)
        World.applyMovements(world.modifyNodes(currentNodes), moves).nodes
    }
