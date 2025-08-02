package model.events.movementEvent

import model.world.EdgeType
import model.world.Types.*
/*
* EdgeMovementConfig defines the configuration for edge movements in the world.
* It includes the probability of movement and the capacity for each edge type.
*/
case class EdgeMovementConfig(
                               probability: Map[EdgeType, Percentage],
                               capacity: Map[EdgeType, Int]
                             )
