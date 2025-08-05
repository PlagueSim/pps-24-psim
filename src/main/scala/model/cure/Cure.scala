package model.cure

/**
 * Factory and builder for [[Cure]] instances.
 */
object Cure:

  /**
   * Creates a new [[Cure]] instance with the given parameters.
   *
   * @param progress Initial cure progress (0.0 to 1.0)
   * @param baseSpeed Initial base research speed (>= 0.0)
   * @param modifiers Modifiers affecting cure progression
   * @return A new [[Cure]] instance
   */
  def apply(
      progress: Double = 0.0,
      baseSpeed: Double = 0.0,
      modifiers: CureModifiers = CureModifiers.empty
  ): Cure =
    require(
      progress >= 0.0 && progress <= 1.0,
      "Progress must be between 0.0 and 1.0"
    )
    require(baseSpeed >= 0.0, "Base speed must be non-negative")
    new Cure(progress, baseSpeed, modifiers)

  /**
   * Returns a new [[CureBuilder]] for incremental construction of a [[Cure]].
   */
  def builder: CureBuilder = new CureBuilder(0.0, 0.0, CureModifiers.empty)

  /**
   * Returns a standard [[Cure]] instance with default parameters using the builder.
   *
   * @return A default [[Cure]] instance
   */
  def StandardCure: Cure =
    builder
      .withProgress(0.0)
      .withBaseSpeed(0.0)
      .withModifiers(CureModifiers.empty)
      .build

  /**
   * Builder for [[Cure]] instances.
   * Allows stepwise configuration of progress, base speed, and modifiers.
   * @param progress Initial cure progress
   * @param baseSpeed Initial base research speed
   * @param modifiers Initial modifiers
   */
  final case class CureBuilder(
      progress: Double = 0.0,
      baseSpeed: Double = 0.0,
      modifiers: CureModifiers = CureModifiers.empty
  ):
    /**
     * Sets the cure progress.
     * @param progress Value in [0.0, 1.0]
     * @return Updated builder
     */
    def withProgress(progress: Double): CureBuilder =
      require(
        progress >= 0.0 && progress <= 1.0,
        "Progress must be between 0.0 and 1.0"
      )
      copy(progress = progress)

    /**
     * Sets the base research speed.
     * @param baseSpeed Non-negative value
     * @return Updated builder
     */
    def withBaseSpeed(baseSpeed: Double): CureBuilder =
      require(baseSpeed >= 0.0, "Base speed must be non-negative")
      copy(baseSpeed = baseSpeed)

    /**
     * Sets the modifiers collection.
     * @param modifiers Modifiers to use
     * @return Updated builder
     */
    def withModifiers(modifiers: CureModifiers): CureBuilder =
      copy(modifiers = modifiers)

    /**
     * Builds the [[Cure]] instance from the builder's state.
     * @return New [[Cure]]
     */
    def build: Cure = Cure(progress, baseSpeed, modifiers)

/**
 * Represents the state of the cure in the simulation.
 * @param progress Current progress (0.0 to 1.0)
 * @param baseSpeed Base research speed (>= 0.0)
 * @param modifiers Modifiers affecting cure progression
 */
final case class Cure private (
    progress: Double = 0.0,
    baseSpeed: Double = 0.0,
    modifiers: CureModifiers = CureModifiers.empty
):
  /**
   * Calculates the effective research speed after applying modifiers.
   * @return Effective speed (>= 0.0)
   */
  def effectiveSpeed: Double =
    modifiers.factors
      .foldLeft(baseSpeed)((speed, factor) => factor(speed))
      .max(0.0)

  /**
   * Advances the cure's progress by one step using the effective speed.
   * @return New [[Cure]] with updated progress
   */
  def advance(): Cure =
    val newProgress = (progress + effectiveSpeed).min(1.0).max(0.0)
    copy(progress = newProgress)

  /**
   * Adds a modifier to the cure. One-time modifiers affect progress immediately.
   * @param mod Modifier to add
   * @return New [[Cure]] with modifier applied
   */
  def addModifier(mod: CureModifier): Cure =
    mod match
      case oneTime: OneTimeModifier
          if !modifiers.modifiers.contains(oneTime.id) =>
        Cure(
          oneTime.apply(progress).min(1.0).max(0.0),
          baseSpeed,
          modifiers.add(oneTime)
        )
      case persistent: PersistentModifier =>
        Cure(progress, baseSpeed, modifiers.add(persistent))
      case _ => this // Ignore if the modifier is already present

  /**
   * Removes a modifier by its ID.
   * @param id Modifier identifier
   * @return New [[Cure]] without the modifier
   */
  def removeModifierById(id: ModifierId): Cure =
    Cure(progress, baseSpeed, modifiers.removeById(id))

  /**
   * Removes all modifiers from a specific source.
   * @param src Source to remove
   * @return New [[Cure]] without those modifiers
   */
  def removeModifiersBySource(src: ModifierSource): Cure =
    Cure(progress, baseSpeed, modifiers.removeBySource(src))

  /**
   * Removes modifiers matching a predicate on their ID.
   * @param pred Predicate for modifier ID
   * @return New [[Cure]] without those modifiers
   */
  def removeModifiersIfId(pred: ModifierId => Boolean): Cure =
    Cure(progress, baseSpeed, modifiers.removeIfId(pred))

/**
 * Collection of modifiers affecting cure progression.
 * @param modifiers Map of modifier IDs to modifiers
 */
final case class CureModifiers(
    modifiers: Map[ModifierId, CureModifier] = Map.empty
):
  /**
   * Returns all persistent modifier functions.
   * @return Iterable of modifier functions
   */
  def factors: Iterable[Double => Double] =
    modifiers.values.collect:
      case mod: PersistentModifier => mod.apply

  private[cure] def add(mod: CureModifier): CureModifiers =
    copy(modifiers = modifiers + (mod.id -> mod))

  private[cure] def removeById(id: ModifierId): CureModifiers =
    copy(modifiers = modifiers - id)

  private[cure] def removeBySource(src: ModifierSource): CureModifiers =
    copy(modifiers = modifiers.filterNot { case (mid, _) => mid.source == src })

  private[cure] def removeIfId(pred: ModifierId => Boolean): CureModifiers =
    copy(modifiers = modifiers.filterNot { case (id, _) => pred(id) })


/**
 * Companion object for [[CureModifiers]]. Provides factory and builder.
 */
object CureModifiers:

  /**
   * An empty collection of cure modifiers.
   */
  val empty: CureModifiers = CureModifiers(Map.empty)

  /**
   * Returns a builder for constructing [[CureModifiers]].
   */
  def builder: CureModifiersBuilder = new CureModifiersBuilder(Map.empty)

  /**
   * Builder for [[CureModifiers]].
   * Allows stepwise addition of modifiers.
   * @param modifiers Initial modifiers map
   */
  final class CureModifiersBuilder private[CureModifiers] (
      private val modifiers: Map[ModifierId, CureModifier]
  ):
    /**
     * Adds a multiplier modifier if valid.
     * @param id Modifier ID
     * @param factor Multiplier value
     * @return Updated builder
     */
    def addMultiplier(
        id: ModifierId,
        factor: Double
    ): CureModifiersBuilder =
      CureModifier.multiplier(id, factor) match
        case Some(mod) => new CureModifiersBuilder(modifiers + (id -> mod))
        case None      => this

    /**
     * Adds an additive modifier if valid.
     * @param id Modifier ID
     * @param amount Additive value
     * @return Updated builder
     */
    def addAdditive(
        id: ModifierId,
        amount: Double
    ): CureModifiersBuilder =
      CureModifier.additive(id, amount) match
        case Some(mod) => new CureModifiersBuilder(modifiers + (id -> mod))
        case None      => this

    /**
     * Adds a progress modifier if valid.
     * @param id Modifier ID
     * @param amount Progress value
     * @return Updated builder
     */
    def addProgressModifier(
        id: ModifierId,
        amount: Double
    ): CureModifiersBuilder =
      CureModifier.progressModifier(id, amount) match
        case Some(mod) => new CureModifiersBuilder(modifiers + (id -> mod))
        case None      => this

    /**
     * Adds multiple modifiers at once.
     * @param modifiers Map of modifiers to add
     * @return Updated builder
     */
    def withModifiers(modifiers: Map[ModifierId, CureModifier]): CureModifiersBuilder =
      new CureModifiersBuilder(this.modifiers ++ modifiers)

    /**
     * Builds the [[CureModifiers]] instance from the builder's state.
     * @return New [[CureModifiers]]
     */
    def build: CureModifiers = CureModifiers(modifiers)
