package model.cure

sealed trait CureModifier:
  def id: ModifierId

sealed trait PersistentModifier extends CureModifier:
  def apply(baseSpeed: Double): Double
  
sealed trait OneTimeModifier extends CureModifier:
  def apply(progress: Double): Double

object CureModifier:
  private def clampToUnitInterval(value: Double): Double = value match
    case v if v < 0.0 => 0.0
    case v if v > 1.0 => 1.0
    case _            => value

  /** Multiplies the base speed by a factor.
    *
    * @param factor
    *   The factor to multiply the base speed by.
    */
  case class Multiplier(id: ModifierId, factor: Double) extends PersistentModifier:
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
    def apply(progress: Double): Double = clampToUnitInterval(progress + amount)
