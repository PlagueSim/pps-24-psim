package model.world

/**
 * Represents how a node moves within the world.
 * This can be static (no movement), random (with a probability),
 * or targeted (towards a specific node with a certain intensity).
 * */

sealed trait MovementStrategy

case object Static extends MovementStrategy

case object RandomNeighbor extends MovementStrategy
