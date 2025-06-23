package model.World

/**
 * Represents how a node moves within the world.
 * This can be static (no movement), random (with a probability),
 * or targeted (towards a specific node with a certain intensity).
 * */

sealed trait MovementStrategy

case object Static extends MovementStrategy

final case class RandomMove(probability: Double) extends MovementStrategy

final case class TargetedMove(targetNode: String, intensity: Double) extends MovementStrategy

object MovementStrategy:

  val DefaultRandom: RandomMove = RandomMove(0.5)

  val DefaultTarget: TargetedMove = TargetedMove("capital", 1.0)

