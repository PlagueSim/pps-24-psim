package model.world

/*
 * Represents how a node moves within the world.
 * This can be static (no movement), RandomNeighbor (with a percentage),
*/

sealed trait MovementStrategy

case object Static extends MovementStrategy

case object LocalPercentageMovement extends MovementStrategy

case object GlobalLogicMovement extends MovementStrategy
