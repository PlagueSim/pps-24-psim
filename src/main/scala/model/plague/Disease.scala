package model.plague

import model.plague.TraitCategory.*

import scala.util.Random
/**
 * TODO
 * @param name
 * @param diseaseType
 * @param traits
 * @param dnaPoints
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
  def infectivity: Double = traits.toList.map(_.infectivity).sum

  /**
   * Calculates the total severity of the [[Disease]].
   *
   * Severity is the sum of the severity values of all currently evolved [[Trait]].
   *
   * @return A [[Double]] representing the [[Disease]] current severity.
   */
  def severity: Double = traits.toList.map(_.severity).sum

  /**
   * Calculates the total lethality of the [[Disease]].
   *
   * Lethality is the sum of the lethality values of all currently evolved [[Trait]].
   *
   * @return A [[Double]] representing the [[Disease]] current lethality.
   */
  def lethality: Double = traits.toList.map(_.lethality).sum

  /**
   * Checks whether the [[Disease]] has already evolved a [[Trait]] with the given name.
   *
   * @param name The name of the [[Trait]] to check for duplicate.
   * @return [[true]] if the [[name]] is present among the evolved traits, [[false]] otherwise.
   */
  private def hasTrait(name: String): Boolean = traits.exists(_.name == name)

  /**
   * Determines whether the given [[Trait]] can be evolved based on its prerequisites.
   *
   * Evolution rules differ by [[TraitCategory]]:
   *  - For [[Symptom]] traits: evolution is allowed if at least one of its prerequisites has already been evolved.
   *  - For [[Transmission]] and [[Ability]] all prerequisites must be met.
   *
   * @param t The [[Trait]] to check if evolution is possible.
   * @return [[true]] if the [[Trait]] can be evolved, [[false]] otherwise.
   */
  private def canEvolve(t: Trait): Boolean = t.category match
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
    val remaining = traits - t
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
   *  - the [[Trait]] is unlocked (its prerequisites are satisfied),
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
      .flatMap(_ => Either.cond(dnaPoints >= traitToAdd.cost, (), s"Not enough DNA points to evolve ${traitToAdd.name}"))
      .map(_ => copy(traits = traits + traitToAdd, dnaPoints = dnaPoints - traitToAdd.cost))


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
   *
   * @param allTraits
   * @return
   */
  def randomMutation(allTraits: Set[Trait]): Disease =
    val unevolved = allTraits.diff(traits)
    val evolvable = unevolved.filter(t => canEvolve(t))

    if evolvable.isEmpty then this
    else
      val chosen = Random.shuffle(evolvable.toList).head
      copy(traits = traits + chosen)

  /**
   *
   * @param points
   * @return
   */
  def addDnaPoints(points: Int): Disease =
    copy(dnaPoints = dnaPoints + points)

/**
 * TODO
 */
object Disease:
  def apply(
             name: String = "pax-12",
             traits: Set[Trait] = Set.empty,
             dnaPoints: Int
           ): Disease = new Disease(name, traits, dnaPoints)
