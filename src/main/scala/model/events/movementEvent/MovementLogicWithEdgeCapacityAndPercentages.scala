package model.events.movementEvent

import model.world.EdgeType

trait MovementLogicWithEdgeCapacityAndPercentages extends MovementLogic:
  val edgeProbabilityMap: Map[EdgeType, Double]
  val edgeTypeCapacityMap: Map[EdgeType, Int]
