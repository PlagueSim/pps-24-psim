package model.events.movementEvent
import model.world.{Edge, Node, World}

trait MovementLogic:
  def compute(
               world: World,
               percent: Double,
               rng: scala.util.Random
             ): List[(String, String, Int)]
