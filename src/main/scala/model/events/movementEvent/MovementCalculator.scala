package model.events.movementEvent

import model.World.{MovementStrategy, Node}

object MovementCalculator:
  def movementsPerStrategy(node: Node, movements: Map[MovementStrategy, Double]): Map[MovementStrategy, Int] =
    movements.map { case (strategy, perc) =>
      (strategy, (node.population * perc).toInt)
    }

