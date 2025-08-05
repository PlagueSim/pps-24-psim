package model.cure

/** Base trait for all cure modifiers
  *
  * @note
  *   Modifiers can be either persistent (applied daily) or one-time (applied
  *   immediately)
  */
sealed trait CureModifier:
  /** Unique identifier for this modifier */
  def id: ModifierId

/** Modifier whose effect is applied repeatedly during cure progression */
sealed trait PersistentModifier extends CureModifier:
  def apply(baseSpeed: Double): Double

/** Modifier whose effect is applied once to cure progress */
sealed trait OneTimeModifier extends CureModifier:
  def apply(progress: Double): Double

object CureModifier:
  private def clampToUnitInterval(value: Double): Double =
    value.max(0.0).min(1.0)

  /** Creates a multiplier modifier if valid
    *
    * @param id
    *   Unique identifier for the modifier
    * @param factor
    *   Multiplication factor (must be >= 0.0)
    * @return
    *   Some(Multiplier) if valid, None otherwise
    */
  def multiplier(id: ModifierId, factor: Double): Option[Multiplier] =
    if factor >= 0.0 then Some(Multiplier(id, factor)) else None

  /** Creates an additive modifier if valid
    *
    * @param id
    *   Unique identifier for the modifier
    * @param amount
    *   Value to add to cure speed (must be in [-1.0, 1.0])
    * @return
    *   Some(Additive) if valid, None otherwise
    */
  def additive(id: ModifierId, amount: Double): Option[Additive] =
    if amount >= -1.0 && amount <= 1.0 then Some(Additive(id, amount)) else None

  /** Creates a progress modifier if valid
    *
    * @param id
    *   Unique identifier for the modifier
    * @param amount
    *   Value to add to cure progress (must be in [-1.0, 1.0])
    * @return
    *   Some(ProgressModifier) if valid, None otherwise
    */
  def progressModifier(
      id: ModifierId,
      amount: Double
  ): Option[ProgressModifier] =
    if amount >= -1.0 && amount <= 1.0 then Some(ProgressModifier(id, amount))
    else None

  private[CureModifier] case class Multiplier(id: ModifierId, factor: Double)
      extends PersistentModifier:
    require(factor >= 0.0, "Factor must be non-negative")
    def apply(baseSpeed: Double): Double = baseSpeed * factor

  private[CureModifier] case class Additive(id: ModifierId, amount: Double)
      extends PersistentModifier:
    def apply(baseSpeed: Double): Double = clampToUnitInterval(
      baseSpeed + amount
    )

  private[CureModifier] case class ProgressModifier(
      id: ModifierId,
      amount: Double
  ) extends OneTimeModifier:
    require(amount >= -1.0 && amount <= 1.0, "Amount must be in [-1.0, 1.0]")
    def apply(progress: Double): Double = clampToUnitInterval(progress + amount)
