package model.events.movementEvent

import model.world.Node

trait GlobalMovementLogic:
  def compute(
               nodes: Map[String, Node],
               peopleToMove: Int,
               neighbors: String => Set[String],
               isEdgeOpen: (String, String) => Boolean,
               rng: scala.util.Random
             ): List[(String, String)]

