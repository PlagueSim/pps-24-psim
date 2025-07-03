package model.cure

/** Represents the state of the cure in the simulation.
  *
  * @param progress
  *   The current progress of the cure, as a value between 0.0 and 1.0.
  * @param baseSpeed
  *   The base speed at which the cure progresses.
  * @param modifiers
  *   Modifiers that can affect the cure's progress speed.
  */
final case class Cure(
    progress: Double = 0.0,
    baseSpeed: Double = 0.01,
    modifiers: CureModifiers = CureModifiers.empty
):
  def effectiveSpeed: Double =
    modifiers.factors.foldRight(baseSpeed) ((modifier, speed) => modifier(speed))
    
  def advance(): Cure =
    val newProgress = (progress + effectiveSpeed).min(1.0)
    this.copy(progress = newProgress)

/** Represents a collection of modifiers that affect the cure's progress.
  *
  * @param factors
  *   The list of modifiers applied to the cure.
  */
final case class CureModifiers(factors: List[CureModifier]):
  /** Adds a new modifier to the collection.
    *
    * @param modifier
    *   The modifier to add.
    * @return
    *   A new CureModifiers instance with the modifier added.
    */
  def add(modifier: CureModifier): CureModifiers =
    CureModifiers(modifier :: factors)

  /** Removes modifiers matching the given filter predicate.
    *
    * @param filter
    *   Predicate to select which modifiers to remove.
    * @return
    *   A new CureModifiers instance with the selected modifiers removed.
    */
  def remove(filter: CureModifier => Boolean): CureModifiers =
    CureModifiers(factors.filterNot(filter))

/** Companion object for [[CureModifiers]].
  */
object CureModifiers:
  /** An empty collection of cure modifiers.
    */
  val empty: CureModifiers = CureModifiers(Nil)

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
