package model.events.movementEvent
import model.world.Node

trait MovementLogic:
  def compute(
               nodes: Map[String, Node],
               percent: Double,
               neighbors: String => Set[String],
               isEdgeOpen: (String, String) => Boolean,
               rng: scala.util.Random
             ): List[(String, String, Int)]
