package model.plague

/**
 * Categories a [[Trait]] can belong to.
 */
enum TraitCategory:
  case Symptom, Transmission, Ability

/**
 * Encapsulation of the statistics of a [[Trait]].
 *
 * @param infectivity    How much the [[Trait]] increases the [[Disease]] infectivity.
 * @param severity       How much the [[Trait]] increases the [[Disease]] severity.
 * @param lethality      How much the [[Trait]] increases the [[Disease]] lethality.
 * @param cost           Evolution cost in DNA points.
 * @param mutationChance How much the [[Trait]] increases the [[Disease]] mutation chance.
 * @param cureSlowdown   Degree to which the trait slows down cure development.
 */
case class TraitStats (
                       infectivity: Double = 0.0,
                       severity: Double = 0.0,
                       lethality: Double = 0.0,
                       cost: Int = 0,
                       mutationChance: Double = 0.0,
                       cureSlowdown: Double = 0.01
                     )

/**
 * Represents a [[Trait]] that can be evolved by a [[Disease]].
 *
 * @param name          Name of the trait.
 * @param category      The category of the [[Trait]], given by [[TraitCategory]].
 * @param stats         The different statistics of the [[Trait]] given by [[TraitStats]].
 * @param prerequisites Set of [[Trait]] names that, depending on [[TraitCategory]] must be unlocked before this one.
 */
case class Trait private (
                           name: String,
                           category: TraitCategory,
                           stats: TraitStats,
                           prerequisites: Set[String]
                         ):

  /**
   * Tests whether the [[Trait]] is a root of a trait tree.
   * 
   * @return [[true]] if the [[Trait]] has no prerequisites, [[false]] otherwise
   */
  def isRoot: Boolean = prerequisites.isEmpty


object Trait:
  /**
   * Constructs a [[Trait]] from the given data.
   *
   * @param name          Name of the [[Trait]].
   * @param category      Category of the [[Trait]].
   * @param stats         Statistics of the [[Trait]].
   * @param prerequisites Required traits for this one be unlockable.
   * @return A new [[Trait]] instance.
   */
  def apply(
             name: String,
             category: TraitCategory,
             stats: TraitStats = TraitStats(),
             prerequisites: Set[String] = Set.empty
           ): Trait = new Trait(name, category, stats, prerequisites)

/**
 * Builder for constructing [[Trait]] instances.
 *
 * @param name          Name of the [[Trait]].
 * @param category      Category of the [[Trait]].
 * @param stats         Initial statistics (can be customized).
 * @param prerequisites Initial prerequisites (can be extended).
 */
case class TraitBuilder(
                         name: String,
                         category: TraitCategory,
                         stats: TraitStats = TraitStats(),
                         prerequisites: Set[String] = Set.empty
                       ):
  /**
   * Builds the final [[Trait]] from this builder.
   *
   * @return A fully constructed Trait.
   */
  def build(): Trait = Trait(name, category, stats, prerequisites)


object TraitDsl:

  extension (tb: TraitBuilder)
    /**
     * Sets the infectivity value.
     */
    def infectivity(v: Double): TraitBuilder = tb.copy(stats = tb.stats.copy(infectivity = v))

    /**
     * Sets the severity value.
     */
    def severity(v: Double): TraitBuilder = tb.copy(stats = tb.stats.copy(severity = v))

    /**
     * Sets the lethality value.
     */
    def lethality(v: Double): TraitBuilder = tb.copy(stats = tb.stats.copy(lethality = v))

    /**
     * Sets the DNA cost.
     */
    def cost(v: Int): TraitBuilder = tb.copy(stats = tb.stats.copy(cost = v))

    /**
     * Sets the mutation chance.
     */
    def mutationChance(v: Double): TraitBuilder = tb.copy(stats = tb.stats.copy(mutationChance = v))

    /**
     * Sets the cure slowdown factor.
     */
    def cureSlowdown(v: Double): TraitBuilder = tb.copy(stats = tb.stats.copy(cureSlowdown = v))

    /**
     * Adds prerequisite trait names.
     */
    def prerequisite(values: String*): TraitBuilder = tb.copy(prerequisites = tb.prerequisites ++ values)

  /**
   * Starts building a new [[Trait]] with the given [[name]] and [[category]].
   *
   * @param name     Name of the [[Trait]].
   * @param category Category of the [[Trait]].
   * @return A new [[TraitBuilder]] instance.
   */
  def define(name: String, category: TraitCategory): TraitBuilder =
    TraitBuilder(name, category)