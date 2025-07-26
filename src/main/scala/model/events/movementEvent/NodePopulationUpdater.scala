package model.events.movementEvent

import model.world.Node
import model.world.MovementStrategy

object NodePopulationUpdater:
  def updateAll(nodes: Map[String, Node], arrivals: Map[String, Int], movements: Map[MovementStrategy, Double]): Map[String, Node] =
    nodes.map { case (id, node) =>
      id -> MovementHelpers.updateNodePopulation(id, node, arrivals, movements)
    }