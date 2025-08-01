package model.world

/*
 * Represents how a node moves within the world.
 * This can be static (no movement), LocalPercentageMovement (with a percentage of the people based on the node population),
 * or GlobalLogicMovement (using a global logic to determine movement).
*/

sealed trait MovementStrategy

case object Static extends MovementStrategy

case object LocalPercentageMovement extends MovementStrategy

case object GlobalLogicMovement extends MovementStrategy
