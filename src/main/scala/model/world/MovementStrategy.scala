package model.world

/*
 * Represents how a node moves within the world.
 * This can be static (no movement), RandomNeighbor (with a percentage),
*/

sealed trait MovementStrategy

sealed trait LocalPercentageMovementStrategy extends MovementStrategy

sealed trait GlobalLogicStrategy extends MovementStrategy

case object Static extends MovementStrategy

case object LocalPercentageMovement extends LocalPercentageMovementStrategy

case object GlobalRandomMovement extends GlobalLogicStrategy
