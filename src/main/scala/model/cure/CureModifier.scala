package model.cure

sealed trait CureModifier:
  def apply(baseSpeed: Double): Double

object CureModifier:
  /** Multiplies the base speed by a factor.
    *
    * @param factor
    *   The factor to multiply the base speed by.
    */
  case class Multiplier(factor: Double) extends CureModifier:
    def apply(baseSpeed: Double): Double = baseSpeed * factor

  /** Adds a fixed amount to the base speed.
    *
    * @param amount
    *   The amount to add to the base speed.
    */
  case class Additive(amount: Double) extends CureModifier:
    def apply(baseSpeed: Double): Double = baseSpeed + amount

  /** Sets a minimum threshold for the base speed.
    *
    * @param min
    *   The minimum speed that the cure can have.
    */
  case class MinThreshold(min: Double) extends CureModifier:
    def apply(baseSpeed: Double): Double = math.max(baseSpeed, min)
