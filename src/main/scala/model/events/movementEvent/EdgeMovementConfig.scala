package model.events.movementEvent

import model.world.EdgeType

case class EdgeMovementConfig(
                               probability: Map[EdgeType, Double],
                               capacity: Map[EdgeType, Int]
                             )
