package model.events.movementEvent

import model.world.EdgeType

class EdgeConfigurationFactory:
  def getDefaultEdgeConfiguration: EdgeMovementConfig =
    EdgeMovementConfig(
      Map(
        EdgeType.Land -> 0.3,
        EdgeType.Sea -> 0.2,
        EdgeType.Air -> 0.15
      ),
      Map(
        EdgeType.Land -> 500,
        EdgeType.Sea -> 200,
        EdgeType.Air -> 100
      )
    )
