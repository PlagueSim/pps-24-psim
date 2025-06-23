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

  def isMobile(strategy: MovementStrategy): Boolean = strategy match
    case Static => false
    case _ => true

  /** Safely creates a RandomMove, enforcing 0.0 <= prob <= 1.0 */
  def random(probability: Double): RandomMove =
    require(probability >= 0.0 && probability <= 1.0, "Probability must be between 0.0 and 1.0")
    RandomMove(probability)

  /** Safely creates a TargetedMove, enforcing 0.0 <= intensity <= 1.0 */
  def targeted(node: String, intensity: Double): TargetedMove =
    require(intensity >= 0.0 && intensity <= 1.0, "Intensity must be between 0.0 and 1.0")
    TargetedMove(node, intensity)
