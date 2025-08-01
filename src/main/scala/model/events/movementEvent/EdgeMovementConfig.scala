package model.events.movementEvent

import model.world.EdgeType
/*
* EdgeMovementConfig defines the configuration for edge movements in the world.
* It includes the probability of movement and the capacity for each edge type.
*/
case class EdgeMovementConfig(
                               probability: Map[EdgeType, Double],
                               capacity: Map[EdgeType, Int]
                             )
