package model.events.movementEvent

import model.world.{Edge, EdgeType, Node, World}

class GlobalLogic extends MovementLogic:

  override def compute(
                        world: World,
                        percent: Double,
                        rng: scala.util.Random
  ): List[(String, String, Int)] = {
    val edgeProbabilityMap: Map[EdgeType, Double] = Map(
      EdgeType.Land -> 0.3,
      EdgeType.Sea -> 0.2,
      EdgeType.Air -> 0.15
    )
    
    List()
  }
