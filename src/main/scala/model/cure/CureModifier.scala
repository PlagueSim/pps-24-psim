package model.cure

sealed trait CureModifier:
  def id: ModifierId

sealed trait PersistentModifier extends CureModifier:
  def apply(baseSpeed: Double): Double
  
sealed trait OneTimeModifier extends CureModifier:
  def apply(progress: Double): Double

object CureModifier:
  private def clampToUnitInterval(value: Double): Double =
    value.max(0.0).min(1.0)

  /** Multiplies the base speed by a factor.
    *
    * @param factor
    *   The factor to multiply the base speed by.
    */
  case class Multiplier(id: ModifierId, factor: Double) extends PersistentModifier:
    require(factor >= 0.0, "Factor must be non-negative")
    def apply(baseSpeed: Double): Double = baseSpeed * factor

  /** Adds a fixed amount to the base speed.
    *
    * @param amount
    *   The amount to add to the base speed.
    */
  case class Additive(id: ModifierId, amount: Double) extends PersistentModifier:
    def apply(baseSpeed: Double): Double = clampToUnitInterval(baseSpeed + amount)

  /** Adds or subtracts a fixed amount to progress.
   * @param amount
   *  The amount to add or subtract from the progress.
   */
  case class ProgressModifier(id: ModifierId, amount: Double) extends OneTimeModifier:
    require(amount >= -1.0 && amount <= 1.0, "Amount must be in the range [-1.0, 1.0]")
    def apply(progress: Double): Double = clampToUnitInterval(progress + amount)
