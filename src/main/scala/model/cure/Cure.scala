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
    modifiers.factors.foldRight(baseSpeed)((modifier, speed) => modifier(speed))

  def advance(): Cure =
    val newProgress = (progress + effectiveSpeed).min(1.0)
    this.copy(progress = newProgress)

/** Represents a collection of modifiers that affect the cure's progress.
  *
  * @param factors
  *   The list of modifiers applied to the cure.
  */
final case class CureModifiers(
    modifiers: Map[ModifierId, CureModifier] = Map.empty
):

  def factors: List[CureModifier] = modifiers.values.toList

  /** Adds a new modifier to the collection.
    *
    * @param modifier
    *   The modifier to add.
    * @return
    *   A new CureModifiers instance with the modifier added.
    */
  def add(mod: CureModifier)(id: ModifierId): CureModifiers =
    copy(modifiers = modifiers + (id -> mod))
  
  /** Removes the modifier with the specified ID from the collection.
   * @param id The ID of the modifier to remove.
   * @return A new CureModifiers instance without the specified modifier.
   */
  def removeById(id: ModifierId): CureModifiers =
    copy(modifiers = modifiers - id)

  /** Removes all modifiers whose ID matches the given predicate.
   * @param pred The predicate to test modifier IDs.
   * @return A new CureModifiers instance without the matching modifiers.
   */
  def removeIfId(pred: ModifierId => Boolean): CureModifiers =
    copy(modifiers = modifiers.filterNot { case (id, _) => pred(id) })

  /** Removes all modifiers whose value matches the given predicate.
   * @param pred The predicate to test modifier values.
   * @return A new CureModifiers instance without the matching modifiers.
   */
  def removeIfMod(pred: CureModifier => Boolean): CureModifiers =
    copy(modifiers = modifiers.filterNot { case (_, m) => pred(m) })

/** Companion object for [[CureModifiers]].
  */
object CureModifiers:
  /** An empty collection of cure modifiers.
    */
  val empty: CureModifiers = CureModifiers(Map.empty)
