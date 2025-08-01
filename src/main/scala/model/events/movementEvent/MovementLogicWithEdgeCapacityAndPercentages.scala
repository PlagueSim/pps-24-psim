package model.events.movementEvent

import model.world.EdgeType

trait MovementLogicWithEdgeCapacityAndPercentages extends MovementLogic:
  def edgeMovementConfig: EdgeMovementConfig
