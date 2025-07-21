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
    modifiers.factors.foldLeft(baseSpeed)((speed, factor) => factor(speed))

  /** Advances the cure's progress by applying the effective speed.
    *
    * @return
    *   A new Cure instance with the updated progress.
    */
  def advance(): Cure =
    val newProgress = progress + effectiveSpeed
    copy(progress =
      math.min(newProgress, 1.0)
    ) // Ensure progress does not exceed 1.0

  def addModifier(mod: CureModifier): Cure =
    mod match
      case oneTime: OneTimeModifier =>
        copy(progress = oneTime.apply(progress), modifiers = modifiers.add(mod))
      case persistent: PersistentModifier =>
        copy(modifiers = modifiers.add(persistent))

  /** Removes a modifier by its ID. */
  def removeModifierById(id: ModifierId): Cure =
    copy(modifiers = modifiers.removeById(id))

  /** Removes all modifiers from a given source. */
  def removeModifiersBySource(src: ModifierSource): Cure =
    copy(modifiers = modifiers.removeBySource(src))

  /** Removes all modifiers matching a predicate on their ID. */
  def removeModifiersIfId(pred: ModifierId => Boolean): Cure =
    copy(modifiers = modifiers.removeIfId(pred))

/** Represents a collection of modifiers that affect the cure's progress.
  *
  * @param modifiers
  *   A map of modifier IDs to their corresponding CureModifier instances.
  */
final case class CureModifiers(
    modifiers: Map[ModifierId, CureModifier] = Map.empty
):

  def factors: Iterable[Double => Double] =
    modifiers.values.collect:
      case mod: PersistentModifier => mod.apply


  /** Adds a new modifier to the collection.
    * @param mod
    *   The modifier to add.
    * @return
    *   A new CureModifiers instance with the added modifier.
    */
  private[cure] def add(mod: CureModifier): CureModifiers =
    copy(modifiers = modifiers + (mod.id -> mod))

  /** Removes the modifier with the specified ID from the collection. */
  private[cure] def removeById(id: ModifierId): CureModifiers =
    copy(modifiers = modifiers - id)

  /** Removes all modifiers whose source matches the given predicate. */
  private[cure] def removeBySource(src: ModifierSource): CureModifiers =
    copy(modifiers = modifiers.filterNot { case (mid, _) => mid.source == src })

  /** Removes all modifiers whose ID matches the given predicate. */
  private[cure] def removeIfId(pred: ModifierId => Boolean): CureModifiers =
    copy(modifiers = modifiers.filterNot { case (id, _) => pred(id) })

/** Companion object for [[CureModifiers]].
  */
object CureModifiers:
  /** An empty collection of cure modifiers.
    */
  val empty: CureModifiers = CureModifiers(Map.empty)
