package model.plague

import model.plague.TraitCategory.*

import scala.util.Random

/**
 * Represents a simulated disease.
 *
 * A [[Disease]] evolves by acquiring new [[Trait]] that increase its infectivity,
 * severity, and lethality. Traits can be [[Symptom]], [[Transmission]], or [[Ability]].
 * DNA points are the currency used to evolve traits.
 *
 * @param name      the name of the disease
 * @param traits    the set of currently evolved traits
 * @param dnaPoints the amount of DNA points available for evolving traits
 */
case class Disease private(
                    name: String,
                    traits: Set[Trait],
                    dnaPoints: Int
                  ):
  /**
   * Calculates the total infectivity of the [[Disease]].
   *
   * Infectivity is the sum of the infectivity values of all currently evolved [[Trait]].
   *
   * @return A [[Double]] representing the [[Disease]] current infectivity.
   */
  def infectivity: Double = traits.toList.map(_.stats.infectivity).sum

  /**
   * Calculates the total severity of the [[Disease]].
   *
   * Severity is the sum of the severity values of all currently evolved [[Trait]].
   *
   * @return A [[Double]] representing the [[Disease]] current severity.
   */
  def severity: Double = traits.toList.map(_.stats.severity).sum

  /**
   * Calculates the total lethality of the [[Disease]].
   *
   * Lethality is the sum of the lethality values of all currently evolved [[Trait]].
   *
   * @return A [[Double]] representing the [[Disease]] current lethality.
   */
  def lethality: Double = traits.toList.map(_.stats.lethality).sum

  /**
   * Calculates the mutation chance of the [[Disease]].
   *
   * Mutation chance is a value between 0 and 1 depending on evolved [[Trait]].
   *
   * @return A [[Double]] representing the [[Disease]] current mutation chance.
   */
  def mutationChance: Double =
    traits.toList.map(_.stats.mutationChance).sum + traits.count(_.category == Symptom) * 0.005

  /**
   *
   * @return
   */
  def allStats(): TraitStats = sum(traits.map(_.stats).toList)

  /**
   *
   * @param stats
   * @return
   */
  private def sum(stats: List[TraitStats]): TraitStats =
    stats.foldLeft(TraitStats())((prev, current) =>
      TraitStats(
        infectivity = prev.infectivity + current.infectivity,
        severity = prev.severity + current.severity,
        lethality = prev.lethality + current.lethality,
        cost = prev.cost + current.cost,
        mutationChance = prev.mutationChance + current.mutationChance,
        cureSlowdown = prev.cureSlowdown + current.cureSlowdown,
        cureReset = prev.cureReset + current.cureReset,
        effectiveness = mergeEffectiveness(prev.effectiveness, current.effectiveness)
      )
    )

  /**
   *
   * @param a
   * @param b
   * @return
   */
  private def mergeEffectiveness(a: Map[Any, Double], b: Map[Any, Double]): Map[Any, Double] =
    (a.keySet ++ b.keySet).map(key =>
      key -> (a.getOrElse(key, 0.0) + b.getOrElse(key, 0.0))
    ).toMap

  /**
   * Checks whether the [[Disease]] has already evolved a [[Trait]] with the given name.
   *
   * @param name The name of the [[Trait]] to check for duplicate.
   * @return [[true]] if the [[name]] is present among the evolved traits, [[false]] otherwise.
   */
  private def hasTrait(name: String): Boolean = traits.exists(_.name == name)

  /**
   * Determines whether the given [[Trait]] can be evolved based on its prerequisite.
   *
   * Evolution rules differ by [[TraitCategory]]:
   *  - For [[Symptom]] traits: evolution is allowed if at least one of its prerequisite has already been evolved.
   *  - For [[Transmission]] and [[Ability]] all prerequisite must be met.
   *
   * @param t The [[Trait]] to check if evolution is possible.
   * @return [[true]] if the [[Trait]] can be evolved, [[false]] otherwise.
   */
  def canEvolve(t: Trait): Boolean = t.category match
    case Symptom => t.prerequisites.exists(hasTrait) || t.prerequisites.isEmpty
    case _ => t.prerequisites.forall(hasTrait)

  /**
   * Determines whether the given [[Trait]] can be involved
   * without leaving any other trait isolated.
   *
   * @param t The [[Trait]] to check if involution is possible
   * @return [[true]] if the [[Trait]] can be involved, [[false]] otherwise
   */
  private def canInvolve(t: Trait): Boolean =
    val remaining = traits.filter(_.category.equals(t.category)) - t
    val traitMap = remaining.iterator.map(tr => tr.name -> tr).toMap
    val roots = remaining.filter(_.isRoot)

    @annotation.tailrec
    def loop(queue: List[String], visited: Set[String]): Set[String] = queue match
      case Nil => visited
      case name :: rest if visited(name) => loop(rest, visited)
      case name :: rest =>
        val children = traitMap.valuesIterator
          .filter(_.prerequisites.contains(name))
          .map(_.name)
          .toList
        loop(rest ++ children, visited + name)

    loop(roots.map(_.name).toList, Set.empty).size == remaining.size


  /**
   * Attempts to evolve a new [[Trait]] for the [[Disease]].
   *
   * The evolution succeeds only if:
   *  - the [[Trait]] has not already been evolved,
   *  - the [[Trait]] is unlocked (its prerequisite are satisfied),
   *  - there are enough DNA points to pay the evolution cost.
   *
   * @param traitToAdd the [[Trait]] to evolve
   * @return [[Either]]:
   *         - [[Right]]: a new evolved [[Disease]]   *
   *         - [[Left]]: an error message
   */

  def evolve(traitToAdd: Trait): Either[String, Disease] =
    Either.cond(!hasTrait(traitToAdd.name), (), s"${traitToAdd.name} already evolved.")
      .flatMap(_ => Either.cond(canEvolve(traitToAdd), (), s"${traitToAdd.name} is locked."))
      .flatMap(_ => Either.cond(dnaPoints >= traitToAdd.stats.cost, (), s"Not enough DNA points to evolve ${traitToAdd.name}"))
      .map(_ => copy(traits = traits + traitToAdd, dnaPoints = dnaPoints - traitToAdd.stats.cost))


  /**
   * Attempts to remove a previously evolved [[Trait]] from the disease.
   *
   * A [[Trait]] can be removed only if:
   *  - it exists in the [[Disease]]
   *  - the removal should not leave any other [[Trait]] isolated
   *
   * Two DNA points are refunded when the [[Trait]] is removed.
   *
   * @param traitToRemove the [[Trait]] to remove
   * @return [[Either]]:
   *         - [[Right]]: a new [[Disease]] with the trait removed
   *         - [[Left]]: an error message
   */
  def involve(traitToRemove: Trait): Either[String, Disease] =
    Either.cond(hasTrait(traitToRemove.name), (), s"${traitToRemove.name} is not currently evolved.")
      .flatMap(_ => Either.cond(canInvolve(traitToRemove), (), s"${traitToRemove.name} cannot be removed because other traits depend on it."))
      .map(_ => copy(traits = traits.filterNot(_.name == traitToRemove.name), dnaPoints = dnaPoints + 2))

  /**
   * Attempts a random mutation by evolving a random [[Trait]] from the set of available traits.
   * Only traits that are not yet evolved and can currently be evolved are considered.
   *
   * @return a new [[Disease]] with the randomly evolved [[Trait]] if possible, otherwise this instance
   */
  def randomMutation(): Disease =
    val allTraits = Symptoms.allBasics
    allTraits.diff(traits.toList).filter(canEvolve) match
      case Nil => this
      case evolvable => copy(traits = traits + Random.shuffle(evolvable).head)

  /**
   * Returns a new [[Disease]] instance with the given number of DNA points added.
   * DNA points are used to evolve traits.
   *
   * @param points the number of DNA points to add
   * @return a new [[Disease]] with updated DNA points
   */
  def addDnaPoints(points: Int): Disease =
    copy(dnaPoints = dnaPoints + points)


object Disease:
  /**
   * Creates a new instance of [[Disease]] with optional default values.
   *
   * @param name      the name of the disease (default is "pax-12")
   * @param traits    the set of initially evolved traits (default is empty)
   * @param dnaPoints the number of starting DNA points (must be provided)
   * @return a new [[Disease]] instance
   */
  def apply(
             name: String = "pax-12",
             traits: Set[Trait] = Set.empty,
             dnaPoints: Int
           ): Disease = new Disease(name, traits, dnaPoints)
