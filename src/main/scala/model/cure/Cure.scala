package model.cure

object Cure:

  def apply(
      progress: Double = 0.0,
      baseSpeed: Double = 0.0,
      modifiers: CureModifiers = CureModifiers.empty
  ): Cure =
    require(progress >= 0.0 && progress <= 1.0, "Progress must be between 0.0 and 1.0")
    require(baseSpeed >= 0.0, "Base speed must be non-negative")
    new Cure(progress, baseSpeed, modifiers)

  def builder: CureBuilder = new CureBuilder(0.0, 0.0, CureModifiers.empty)

  final class CureBuilder private[Cure] (
    private val progress: Double,
    private val baseSpeed: Double,
    private val modifiers: CureModifiers
  ):
    def withProgress(progress: Double): CureBuilder =
      require(progress >= 0.0 && progress <= 1.0, "Progress must be between 0.0 and 1.0")
      new CureBuilder(progress, baseSpeed, modifiers)

    def withBaseSpeed(baseSpeed: Double): CureBuilder =
      require(baseSpeed >= 0.0, "Base speed must be non-negative")
      new CureBuilder(progress, baseSpeed, modifiers)

    def withModifiers(modifiers: CureModifiers): CureBuilder =
      new CureBuilder(progress, baseSpeed, modifiers)

    def build: Cure = Cure(progress, baseSpeed, modifiers)

/** Represents the state of the cure in the simulation.
  *
  * @param progress
  *   The current progress of the cure, as a value between 0.0 and 1.0.
  * @param baseSpeed
  *   The base speed at which the cure progresses.
  * @param modifiers
  *   Modifiers that can affect the cure's progress speed.
  */
final case class Cure private(
    progress: Double = 0.0,
    baseSpeed: Double = 0.0,
    modifiers: CureModifiers = CureModifiers.empty
):
  def effectiveSpeed: Double =
    modifiers.factors
      .foldLeft(baseSpeed)((speed, factor) => factor(speed))
      .max(0.0)

  /** Advances the cure's progress by applying the effective speed.
    *
    * @return
    *   A new Cure instance with the updated progress.
    */
  def advance(): Cure =
    val newProgress = (progress + effectiveSpeed).min(1.0).max(0.0)
    copy(progress = newProgress)

  def addModifier(mod: CureModifier): Cure =
    mod match
      case oneTime: OneTimeModifier if !modifiers.modifiers.contains(oneTime.id) =>
        Cure(oneTime.apply(progress).min(1.0).max(0.0), baseSpeed, modifiers.add(oneTime))
      case persistent: PersistentModifier =>
        Cure(progress, baseSpeed, modifiers.add(persistent))
      case _ => this // Ignore if the modifier is already present

  /** Removes a modifier by its ID. */
  def removeModifierById(id: ModifierId): Cure =
    Cure(progress, baseSpeed, modifiers.removeById(id))

  def removeModifiersBySource(src: ModifierSource): Cure =
    Cure(progress, baseSpeed, modifiers.removeBySource(src))

  def removeModifiersIfId(pred: ModifierId => Boolean): Cure =
    Cure(progress, baseSpeed, modifiers.removeIfId(pred))

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

  def builder: CureModifiersBuilder = new CureModifiersBuilder(Map.empty)

  final class CureModifiersBuilder private[CureModifiers] (
      private val modifiers: Map[ModifierId, CureModifier]
  ):
    def addMultiplier(
        id: ModifierId,
        factor: Double
    ): CureModifiersBuilder =
      CureModifier.multiplier(id, factor) match
        case Some(mod) => new CureModifiersBuilder(modifiers + (id -> mod))
        case None => this

    def addAdditive(
        id: ModifierId,
        amount: Double
    ): CureModifiersBuilder =
      CureModifier.additive(id, amount) match
        case Some(mod) => new CureModifiersBuilder(modifiers + (id -> mod))
        case None => this

    def addProgressModifier(
        id: ModifierId,
        amount: Double
    ): CureModifiersBuilder =
      CureModifier.progressModifier(id, amount) match
        case Some(mod) => new CureModifiersBuilder(modifiers + (id -> mod))
        case None => this

    def build: CureModifiers = CureModifiers(modifiers)
